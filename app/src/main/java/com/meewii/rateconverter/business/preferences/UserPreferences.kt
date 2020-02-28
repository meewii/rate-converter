package com.meewii.rateconverter.business.preferences

import android.content.SharedPreferences
import com.meewii.rateconverter.ui.Order
import dagger.Reusable
import javax.inject.Inject

@Reusable
class UserPreferences @Inject constructor(private val sharedPreferences: SharedPreferences) {

  companion object {
    private const val SORTING_ORDER_KEY = "SORTING_ORDER_KEY"
    private const val LAST_USER_INPUT_KEY = "LAST_USER_INPUT_KEY"
    private const val BASE_CURRENCY_KEY = "BASE_CURRENCY_KEY"
    private const val PINED_CURRENCY_KEY = "PINED_CURRENCY_KEY"
  }

  fun setSortingOrder(order: Order) {
    sharedPreferences.edit().putString(SORTING_ORDER_KEY, order.toString()).apply()
  }

  fun getSortingOrder(): Order {
    return sharedPreferences.getString(SORTING_ORDER_KEY, null)?.let {
      Order.valueOf(it)
    } ?: Order.DEFAULT
  }

  fun setLastUserInput(userInput: Double) {
    sharedPreferences.edit().putString(LAST_USER_INPUT_KEY, userInput.toString()).apply()
  }

  fun getLastUserInput(): Double {
    return sharedPreferences.getString(LAST_USER_INPUT_KEY, "1.0")?.toDoubleOrNull() ?: 1.0
  }

  fun setBaseCurrency(currencyCode: String) {
    sharedPreferences.edit().putString(BASE_CURRENCY_KEY, currencyCode).apply()
  }

  fun getBaseCurrency(): String {
    return sharedPreferences.getString(BASE_CURRENCY_KEY, "EUR") ?: "EUR"
  }

  fun savePinedCurrencies(currencyCodes: Set<String>) {
    sharedPreferences.edit().putStringSet(PINED_CURRENCY_KEY, currencyCodes).apply()
  }

  fun getPinedCurrencies(): Set<String> {
    return sharedPreferences.getStringSet(PINED_CURRENCY_KEY, setOf()) ?: setOf()
  }

}