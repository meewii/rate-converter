package com.meewii.rateconverter.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.meewii.rateconverter.business.RateRepository
import com.meewii.rateconverter.business.RateResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * ViewModel of MainActivity
 */
class MainViewModel @Inject constructor(private val repository: RateRepository) : ViewModel() {

  private val _viewStatus: MutableLiveData<ViewStatus> = MutableLiveData()
  val viewStatus: LiveData<ViewStatus> = _viewStatus

  private val _rateList: MutableLiveData<List<Rate>> = MutableLiveData()
  val rateList: LiveData<List<Rate>> = _rateList

  private var serviceDisposable = Disposables.disposed()

  fun subscribeToRates(baseCurrency: String = "EUR") {
    _viewStatus.value = ViewStatus.Loading
    serviceDisposable = repository.getRatesForBase(baseCurrency)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ response ->
          when (response) {
            is RateResponse.Success -> {
              _rateList.value = response.rates
              _viewStatus.value = ViewStatus.Idle
            }
            is RateResponse.Error -> {
              _rateList.value = null
              _viewStatus.value = ViewStatus.Error(response.errorMessage, response.error)
            }
          }
        }, {
          _rateList.value = null
          _viewStatus.value = ViewStatus.Error(null, it)
        })
  }

  override fun onCleared() {
    super.onCleared()
    serviceDisposable.dispose()
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