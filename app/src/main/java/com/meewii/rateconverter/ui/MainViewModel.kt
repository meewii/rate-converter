package com.meewii.rateconverter.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposables
import javax.inject.Inject

/**
 * ViewModel of MainActivity
 */
class MainViewModel @Inject constructor() : ViewModel() {

  private val _viewStatus: MutableLiveData<ViewStatus> = MutableLiveData()
  val viewStatus: LiveData<ViewStatus> = _viewStatus

  private val _rateList: MutableLiveData<List<Rate>> = MutableLiveData()
  val rateList: LiveData<List<Rate>> = _rateList

  private var serviceDisposable = Disposables.disposed()

  private fun updateList(rates: List<Rate>) {
    _rateList.value = rates
  }

  // TODO subscribe to list

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
  object Success : ViewStatus()
  object Loading : ViewStatus()
  data class Error(val message: String? = null, val throwable: Throwable? = null) : ViewStatus()
}