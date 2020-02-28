package com.meewii.rateconverter.business

import com.meewii.rateconverter.business.preferences.UserPreferences
import dagger.Reusable
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

@Reusable
class PinedCurrenciesRepository @Inject constructor(private val userPreferences: UserPreferences) {

  private val pinnedCurrenciesSubject = BehaviorSubject.createDefault(userPreferences.getPinedCurrencies())

  fun togglePinCurrency(currencyCode: String, isPinned: Boolean) {
    if (isPinned) {
      pinCurrency(currencyCode)
    } else {
      unpinCurrency(currencyCode)
    }
  }

  private fun pinCurrency(currencyCode: String) {
    val set = userPreferences.getPinedCurrencies().toMutableSet()
    set.add(currencyCode)
    userPreferences.savePinedCurrencies(set)
    pinnedCurrenciesSubject.onNext(set)
  }

  private fun unpinCurrency(currencyCode: String) {
    val set = userPreferences.getPinedCurrencies().toMutableSet()
    set.remove(currencyCode)
    userPreferences.savePinedCurrencies(set)
    pinnedCurrenciesSubject.onNext(set)
  }

  /**
   * Returns last set of pinned currencies
   */
  fun getPinedCurrencies(): Flowable<Set<String>> {
    return pinnedCurrenciesSubject.toFlowable(BackpressureStrategy.LATEST)
  }

}
