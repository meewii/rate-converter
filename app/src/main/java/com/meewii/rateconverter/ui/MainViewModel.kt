package com.meewii.rateconverter.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.meewii.rateconverter.business.RateRepository
import com.meewii.rateconverter.business.RateResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel of MainActivity
 */
class MainViewModel @Inject constructor(private val repository: RateRepository) : ViewModel() {

  private val _viewStatus: MutableLiveData<ViewStatus> = MutableLiveData()
  val viewStatus: LiveData<ViewStatus> = _viewStatus

  private val _userInputValue: MutableLiveData<Double> = MutableLiveData()
  private val _rateList: MutableLiveData<List<Rate>> = MutableLiveData()
  val combinedRates = MediatorLiveData<List<Rate>>()

  private var serviceDisposable = Disposables.disposed()

  private var cachedBaseCurrency: String = "EUR"

  fun subscribeToRates(currency: String? = null) {
    _viewStatus.value = ViewStatus.Loading

    cachedBaseCurrency = currency ?: "EUR"

    Timber.d("AAAA currency? $currency | cachedBaseCurrency? $cachedBaseCurrency")

    serviceDisposable.dispose()
    serviceDisposable = repository.getRatesForBase(cachedBaseCurrency)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ response ->
          when (response) {
            is RateResponse.Success -> {
              _rateList.value = response.rates
              _viewStatus.value = ViewStatus.Idle
            }
            is RateResponse.Error -> {
              _viewStatus.value = ViewStatus.Error(response.errorMessage, response.error)
            }
          }
        }, {
          _viewStatus.value = ViewStatus.Error(null, it)
        })
  }

  fun initCombinedRates() {
    combinedRates.addSource(_rateList) { rates ->
      combinedRates.value = combineLatestData(rates, _userInputValue.value ?: 1.0)
    }
    combinedRates.addSource(_userInputValue) { userInput ->
      combinedRates.value = combineLatestData(_rateList.value ?: emptyList(), userInput)
    }
  }

  private fun combineLatestData(rates: List<Rate>, userInput: Double): List<Rate> {
    rates.map {
      it.calculatedValue = userInput * it.rateValue
    }
    return rates
  }

  override fun onCleared() {
    super.onCleared()
    serviceDisposable.dispose()
  }

  fun newUserInput(value: Double) {
    _userInputValue.value = value
  }

}

/**
 * Enhanced enum to reflect the status of the background process to the view
 */
sealed class ViewStatus {
  object Idle : ViewStatus()
  object Loading : ViewStatus()
  data class Error(val message: String? = null, val throwable: Throwable? = null) : ViewStatus()
}