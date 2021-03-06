package com.meewii.rateconverter.ui

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.meewii.rateconverter.business.ExchangeRateRepository
import com.meewii.rateconverter.business.PinedCurrenciesRepository
import com.meewii.rateconverter.business.RateList
import com.meewii.rateconverter.business.UserInputRepository
import com.meewii.rateconverter.business.preferences.UserPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel of MainActivity
 *
 * Combines 2 sources of information to display the list:
 * - the rate list itself from the `ExchangeRateRepository`
 * - the sorting order from direct user action or from the `UserPreferences`
 *
 * This view model also takes care of the status of the view: Idle, Loading or Error
 */
class MainViewModel @Inject constructor(
  private val exchangeRateRepository: ExchangeRateRepository,
  private val userInputRepository: UserInputRepository,
  private val pinedCurrenciesRepository: PinedCurrenciesRepository,
  private val userPreferences: UserPreferences
) : ViewModel() {

  @VisibleForTesting
  internal var cachedBaseCurrency: String = "EUR"

  private val _viewStatus: MutableLiveData<ViewStatus> = MutableLiveData()
  val viewStatus: LiveData<ViewStatus> = _viewStatus

  private val _baseCurrency: MutableLiveData<Currency> = MutableLiveData<Currency>().apply {
    this.value = Currency.toCurrency(cachedBaseCurrency, 1.0)
  }
  val baseCurrency: LiveData<Currency> = _baseCurrency
  private val _lastUserInput = MediatorLiveData<Double>()
  val lastUserInput: LiveData<Double> = _lastUserInput

  private val _combinedRates: MutableLiveData<List<Currency>> = MutableLiveData()
  private val _sortingOrder = MutableLiveData<Order>()
  private val _sortedRates = MediatorLiveData<Currencies>()
  val sortedRates: LiveData<Currencies> = _sortedRates

  init {
    _sortedRates.addSource(_combinedRates) { result: List<Currency>? ->
      result?.let {
        _sortedRates.value = sortCurrencies(
          it,
          _sortingOrder.value ?: userPreferences.getSortingOrder(),
          SourceType.RATE_VALUES
        )
      }
    }
    _sortedRates.addSource(_sortingOrder) { order: Order? ->
      _sortedRates.value = sortCurrencies(
        _combinedRates.value ?: emptyList(),
        order ?: userPreferences.getSortingOrder(),
        SourceType.SORTING
      )
    }
  }

  private var combinedRatesDisposable = Disposables.disposed()
  private var apiRatesDisposable = Disposables.disposed()

  /**
   * Get rates for the given base currency
   */
  fun subscribeToRates(baseCurrency: String? = null) {
    _viewStatus.value = ViewStatus.Loading
    _lastUserInput.value = userPreferences.getLastUserInput()

    val currency = getBaseCurrency(baseCurrency)
    combinedRatesDisposable.dispose()
    combinedRatesDisposable = exchangeRateRepository
      .getCombinedRates(currency)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ handleSuccessfulResponse(it) }, { handleFailedResponse(it) })
  }

  /**
   * Get rates for the given base currency
   */
  fun forceRefreshRates() {
    _viewStatus.value = ViewStatus.Loading

    apiRatesDisposable.dispose()
    apiRatesDisposable = exchangeRateRepository.getCombinedRates(cachedBaseCurrency)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ handleSuccessfulResponse(it) }, { handleFailedResponse(it) })
  }

  private fun handleSuccessfulResponse(rateList: RateList) {
    _combinedRates.value = rateList.currencies
    _viewStatus.value = ViewStatus.Idle
    updateBaseCurrency(rateList.baseCurrency)
  }

  private fun getBaseCurrency(baseCurrency: String? = null): String {
    return baseCurrency ?: userPreferences.getBaseCurrency()
  }

  private fun updateBaseCurrency(currency: String? = null) {
    cachedBaseCurrency = currency ?: "EUR"
    _baseCurrency.value = Currency.toCurrency(cachedBaseCurrency, 1.0)
    userPreferences.setBaseCurrency(cachedBaseCurrency)
  }

  private fun handleFailedResponse(error: Throwable) {
    Timber.e(error, "Error $error")
    _viewStatus.value = ViewStatus.Error(throwable = error)
  }

  /**
   * Set value of the base rate
   */
  fun setBaseRateValue(value: Double) {
    userInputRepository.setBaseRateValue(value)
  }

  /**
   * Set sorting order of the currency list
   */
  fun setOrder(order: Order) {
    _sortingOrder.value = order
    userPreferences.setSortingOrder(order)
  }

  private fun sortCurrencies(
    list: List<Currency>,
    order: Order,
    sourceType: SourceType
  ): Currencies {
    return Currencies(when (order) {
      Order.NAME -> list.sortedWith(compareByDescending<Currency> { it.isPinned }.thenBy { it.currencyCode })
      Order.ASCENDING_RATE -> list.sortedWith(compareByDescending<Currency> { it.isPinned }.thenBy { it.calculatedValue })
      Order.DESCENDING_RATE -> list.sortedWith(compareByDescending<Currency> { it.isPinned }.thenByDescending { it.calculatedValue })
      else -> list.sortedByDescending { it.isPinned }
    }, sourceType)
  }

  override fun onCleared() {
    super.onCleared()
    _sortedRates.removeSource(_combinedRates)
    combinedRatesDisposable.dispose()
  }

  /**
   * Add or Remove currency code from pinned list
   */
  fun togglePinCurrency(currencyCode: String, isPinned: Boolean) {
    pinedCurrenciesRepository.togglePinCurrency(currencyCode, isPinned)
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