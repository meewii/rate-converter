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
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import timber.log.Timber
import javax.inject.Inject

/**
 * Repository in charge of fetching, storing and combining the rates with the user inputs to a RateList model.
 *
 * Combines 4 sources of data to display rate information:
 * - the server data coming as a list of rates
 * - the persisted data
 * - the user input that serves as base rate multiplier
 * - if the currency was pinned by the user
 */
@Reusable
class ExchangeRateRepository @Inject constructor(
  private val service: ExchangeRateService,
  private val exchangeRateDao: ExchangeRateDao,
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

  private fun getRateListForBase(base: String): Flowable<RateList> {
    return getApiRates(base).onErrorResumeNext(getDbRates(base))
  }

  private fun combineRates(
    rateList: RateList,
    userInput: Double,
    pinnedCurrencies: Set<String>
  ): RateList {
    rateList.currencies.map {
      it.calculatedValue = userInput * it.rateValue
      it.isPinned = pinnedCurrencies.contains(it.currencyCode)
    }
    return rateList
  }

  @VisibleForTesting
  internal fun getApiRates(base: String): Flowable<RateList> {
    return service.getRatesForBase(base)
      .subscribeOn(Schedulers.io())
      .toFlowable()
      .flatMap { response ->
        if (response.errorMessage != null) {
          Flowable.error(ResponseErrorException(response.errorMessage))
        } else if (response.base == null || response.rates == null || response.date == null) {
          Flowable.error(InvalidResponseException("The Response is invalid, missing rates, date or base currency"))
        } else {
          val filteredRates = response.rates.toMutableMap().apply { remove(base) }
          // The European Central Bank usually rates only once a day around 16:00 CET
          val ldt = LocalDateTime.of(response.date, LocalTime.of(16, 0))
          persistExchangeRates(response.base, filteredRates, ldt)
          Flowable.just(RateList(response.base, toCurrencyList(filteredRates)))
        }
      }
  }

  @VisibleForTesting
  internal fun getDbRates(base: String): Flowable<RateList> {
    return exchangeRateDao.getRatesForBase(base)
      .subscribeOn(Schedulers.io())
      .map { RateList(it.id, toCurrencyList(it.rates)) }
      .toFlowable()
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

data class RateList(val baseCurrency: String, val currencies: List<Currency>)