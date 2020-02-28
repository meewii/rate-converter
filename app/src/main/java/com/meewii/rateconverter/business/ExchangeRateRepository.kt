package com.meewii.rateconverter.business

import androidx.annotation.VisibleForTesting
import com.meewii.rateconverter.business.database.ExchangeRateDao
import com.meewii.rateconverter.business.database.ExchangeRateEntity
import com.meewii.rateconverter.business.network.ExchangeRateService
import com.meewii.rateconverter.ui.Currency
import com.meewii.rateconverter.ui.Currency.Companion.toCurrencyList
import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Clock
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit
import timber.log.Timber
import javax.inject.Inject

/**
 * Repository in charge of fetching, storing and combining the rates with the user input.
 *
 * Combines 3 sources of data to display rate information:
 * - the server data coming as a list of rates
 * - the persisted data
 * - the user input that serves as base rate multiplier
 * - if the currency was pinned by the user
 */
@Reusable
class ExchangeRateRepository @Inject constructor(
  private val service: ExchangeRateService,
  private val exchangeRateDao: ExchangeRateDao,
  private val clock: Clock,
  private val userInputRepository: UserInputRepository,
  private val pinedCurrenciesRepository: PinedCurrenciesRepository
) {

  private var persistRatesDisposable = Disposables.disposed()

  /**
   * Get combined rates between user inputs and currency rate value
   */
  fun getCombinedRates(base: String): Flowable<RateList> {
    return Flowable.combineLatest<RateList, Double, Set<String>, RateList>(
      getRateListForBase(base),
      userInputRepository.getUserInputsStream(),
      pinedCurrenciesRepository.getPinedCurrencies(),
      Function3 { rateList: RateList, userInput: Double, pinnedCurrencies: Set<String> ->
        combineRates(rateList, userInput, pinnedCurrencies)
      })
  }

  /**
   * Get rates from API combined with user inputs
   */
  fun getApiCombinedRates(base: String): Flowable<RateList> {
    return Flowable.combineLatest<RateList, Double, Set<String>, RateList>(
      getApiRates(base).toFlowable(),
      userInputRepository.getUserInputsStream(),
      pinedCurrenciesRepository.getPinedCurrencies(),
      Function3 { rateList: RateList, userInput: Double, pinnedCurrencies: Set<String> ->
        combineRates(rateList, userInput, pinnedCurrencies)
      })
  }

  private fun getRateListForBase(base: String): Flowable<RateList> {
    return getDbRates(base).switchIfEmpty(getApiRates(base)).toFlowable()
  }

  private fun combineRates(
    rateList: RateList,
    userInput: Double,
    pinnedCurrencies: Set<String>
  ): RateList {
    return when (rateList) {
      is RateList.Success -> {
        rateList.currencies.map {
          it.calculatedValue = userInput * it.rateValue
          it.isPinned = pinnedCurrencies.contains(it.currencyCode)
        }
        rateList
      }
      else -> rateList
    }
  }

  @VisibleForTesting
  internal fun getApiRates(base: String): Single<RateList> {
    return service.getRatesForBase(base)
      .subscribeOn(Schedulers.io())
      .map { response ->
        if (response.errorMessage != null) RateList.Error(null, response.errorMessage)
        else if (response.base == null || response.rates == null || response.date == null) {
          RateList.Error(InvalidResponseException("The Response is invalid, missing rates, date or base currency"))
        } else {
          val filteredRates = response.rates.toMutableMap().apply { remove(base) }
          // The European Central Bank usually rates only once a day around 16:00 CET
          val ldt = LocalDateTime.of(response.date, LocalTime.of(16, 0))
          persistExchangeRates(response.base, filteredRates, ldt)
          RateList.Success(toCurrencyList(filteredRates))
        }
      }
      .doOnSuccess { Timber.d("Dispatch API rate list") }
  }

  @VisibleForTesting
  internal fun getDbRates(base: String): Maybe<RateList> {
    return exchangeRateDao.getRatesForBase(base)
      .subscribeOn(Schedulers.io())
      .filter { entity ->
        val dayInMinutes = 24 * 60
        val untilNow: Long = entity.updatedAt.until(LocalDateTime.now(clock), ChronoUnit.MINUTES)
        untilNow < dayInMinutes
      }
      .map<RateList> {
        RateList.Success(toCurrencyList(it.rates))
      }
      .doOnSuccess { Timber.d("Dispatch DB rate list") }
  }

  private fun persistExchangeRates(base: String, rates: Map<String, Double>, date: LocalDateTime) {
    persistRatesDisposable = Completable.fromAction { exchangeRateDao.insert(ExchangeRateEntity(base, date, rates)) }
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
      .subscribe({
        Timber.i("Insert ExchangeRateEntity done")
      }, {
        Timber.e("Insert ExchangeRateEntity failed: ${it.message}")
      })
  }

}

class InvalidResponseException(override val message: String?) : Exception(message)

sealed class RateList {
  data class Error(val error: Throwable? = null, val errorMessage: String? = null) : RateList()
  data class Success(val currencies: List<Currency>) : RateList()
}