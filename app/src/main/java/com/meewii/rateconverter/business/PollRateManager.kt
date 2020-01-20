package com.meewii.rateconverter.business

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.meewii.rateconverter.business.network.RateService
import com.meewii.rateconverter.core.flagMapper
import com.meewii.rateconverter.core.nameMapper
import com.meewii.rateconverter.ui.Rate
import dagger.Reusable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Manager in charge of polling the rates every seconds.
 * It feeds a RateResponse as LiveData which handles Success and Error states.
 *
 * The polling will retry silently 10 times in case of errors. After reaching maximum attempts, a
 * TooManyAttemptsException will be thrown.
 */
@Reusable
class PollRateManager @Inject constructor(private val service: RateService) {

  companion object {
    const val DEFAULT_CURRENCY = "EUR"
    private const val POLL_INTERVAL_SEC = 1L
    private const val ERROR_ATTEMPTS = 10     // TBD: exact value to be defined in Acceptance Criteria
    private const val RETRY_DELAY_SEC = 3L    // TBD: exact value to be defined in Acceptance Criteria
  }

  private val _rateResponse: MutableLiveData<RateResponse> = MutableLiveData()
  val rateResponse: LiveData<RateResponse> = _rateResponse

  private var pollDisposable = Disposables.disposed()

  private var attemptCount = 0

  @VisibleForTesting
  internal var cachedBaseCurrency = DEFAULT_CURRENCY
  @VisibleForTesting
  internal var shouldKeepPinging = false

  /**
   * Start polling rates with given base currency every 1s
   */
  fun startPollingRates(base: String) {
    attemptCount = 0
    cachedBaseCurrency = base
    poll(cachedBaseCurrency)
  }

  /**
   * Stop polling rates
   */
  fun stopPollingRates() {
    attemptCount = 0
    pollDisposable.dispose()
  }

  @VisibleForTesting
  internal fun poll(base: String) {
    shouldKeepPinging = true

    pollDisposable.dispose()
    pollDisposable = Flowable.interval(POLL_INTERVAL_SEC, TimeUnit.SECONDS)
      .flatMap { service.getRatesForBase(base).toFlowable() }
      .map { response ->
        if (response.errorMessage != null) RateResponse.Error(null, response.errorMessage)
        else if (response.base == null || response.rates == null) {
          RateResponse.Error(InvalidResponseException("The Response is invalid, missing rates or base currency"))
        } else {
          RateResponse.Success(toRateList(response.base, response.rates))
        }
      }
      .retryWhen { errors ->
        errors.flatMap {
          if (++attemptCount < ERROR_ATTEMPTS) {
            Timber.w("Retrying after attempt number ${attemptCount}.")
            // TBD in ACs: should user be informed right away or only after all retries were attempted?
            Flowable.timer(RETRY_DELAY_SEC, TimeUnit.SECONDS, Schedulers.io())
          } else {
            Timber.w("Too many attempts, sending error.")
            Flowable.error(TooManyAttemptsException("Too many polling errors"))
          }
        }
      }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ rateResponse ->
        _rateResponse.value = rateResponse
        attemptCount = 0
      }, {
        Timber.e(it, "Error $it")
        _rateResponse.value = RateResponse.Error(it)
      })
  }

  /**
   * Maps the map of rates from the Response to Rate list
   */
  private fun toRateList(base: String, rates: Map<String, Double>): List<Rate> {
    val mutableList = mutableListOf<Rate>()

    mutableList.add(
      Rate(position = 0, currencyCode = base, flagResId = flagMapper(base), nameResId = nameMapper(base))
    )

    rates.forEach {
      val rate = Rate(
        currencyCode = it.key, rateValue = it.value, flagResId = flagMapper(it.key), nameResId = nameMapper(it.key)
      )
      mutableList.add(rate)
    }
    return mutableList
  }

}

class TooManyAttemptsException(override val message: String?) : Exception(message)
class InvalidResponseException(override val message: String?) : Exception(message)

sealed class RateResponse {
  data class Error(val error: Throwable? = null, val errorMessage: String? = null) : RateResponse()
  data class Success(val rates: List<Rate>) : RateResponse()
}