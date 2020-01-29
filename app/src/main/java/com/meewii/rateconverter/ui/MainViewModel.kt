package com.meewii.rateconverter.ui

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.meewii.rateconverter.business.PollRateManager
import com.meewii.rateconverter.business.PollRateManager.Companion.DEFAULT_CURRENCY
import com.meewii.rateconverter.business.RateResponse
import javax.inject.Inject

/**
 * ViewModel of MainActivity
 * Combines 2 sources of data to display rate information:
 * - the server data coming as a list of rates
 * - the user input that serves as base rate multiplier
 * This view model also takes care of the status of the view: Idle, Loading or Error
 */
class MainViewModel @Inject constructor(private val pollManager: PollRateManager) : ViewModel() {

  private val _viewStatus: MutableLiveData<ViewStatus> = MutableLiveData()
  val viewStatus: LiveData<ViewStatus> = _viewStatus

  private val _userInputValue: MutableLiveData<Double> = MutableLiveData()
  val combinedRates = MediatorLiveData<List<Rate>>()

  @VisibleForTesting
  internal var cachedBaseCurrency: String? = null

  /**
   * Start polling rates for the given base currency
   */
  fun subscribeToRates(currency: String? = null) {
    _viewStatus.value = ViewStatus.Loading
    cachedBaseCurrency = currency ?: cachedBaseCurrency
    pollManager.startPollingRates(cachedBaseCurrency ?: DEFAULT_CURRENCY)
  }

  /**
   * Value of the base rate entered by the user
   */
  fun newUserInput(value: Double) {
    _userInputValue.value = value
  }

  fun initCombinedRates() {
    combinedRates.addSource(pollManager.rateResponse) { rates ->
      combineLatestData(rates, _userInputValue.value ?: 1.0)
    }
    combinedRates.addSource(_userInputValue) { userInput ->
      combineLatestData(pollManager.rateResponse.value, userInput)
    }
  }

  @VisibleForTesting
  internal fun combineLatestData(response: RateResponse?, userInput: Double) {
    var rates: List<Rate>? = combinedRates.value
    when (response) {
      is RateResponse.Success -> {
        rates = response.rates
        _viewStatus.value = ViewStatus.Idle
      }
      is RateResponse.Error -> {
        _viewStatus.value = ViewStatus.Error(response.errorMessage, response.error)
      }
      else -> {
        rates = emptyList()
      }
    }

    rates?.map {
      it.calculatedValue = userInput * it.rateValue
    }
    combinedRates.value = rates
  }

  fun stopPollingRates() {
    pollManager.stopPollingRates()
  }

  override fun onCleared() {
    super.onCleared()
    pollManager.stopPollingRates()
  }

}

/**
 * Enhanced enum to reflect the status of the background process to the view
 */
sealed class ViewStatus {
  /**
   * Success or Nothing
   */
  object Idle : ViewStatus()

  /**
   * The view model is not yet ready to provide displayable data
   */
  object Loading : ViewStatus()

  /**
   * Something went wrong
   */
  data class Error(val message: String? = null, val throwable: Throwable? = null) : ViewStatus()
}