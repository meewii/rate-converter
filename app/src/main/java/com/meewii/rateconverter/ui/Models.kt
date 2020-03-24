package com.meewii.rateconverter.ui

import com.meewii.rateconverter.R
import com.meewii.rateconverter.common.flagMap
import com.meewii.rateconverter.common.flagMapper
import com.meewii.rateconverter.common.nameMapper

data class Currency(
  val currencyCode: String,
  var rateValue: Double = 1.0,
  val flagResId: Int,
  val nameResId: Int
) {

  var calculatedValue: Double = rateValue
  var isPinned: Boolean = false

  companion object {

    private val defaultBaseCurrency = Currency(
      currencyCode = "EUR", rateValue = 1.0,
      flagResId = R.drawable.ic_flag_eur,
      nameResId = R.string.currency_name_EUR
    )

    /**
     * Maps a pair of code and rate value to Currency
     */
    fun toCurrency(code: String, value: Double): Currency {
      if (!flagMap.keys.contains(code)) return defaultBaseCurrency
      return Currency(
        currencyCode = code, rateValue = value, flagResId = flagMapper(code), nameResId = nameMapper(code)
      )
    }

    /**
     * Maps the map of rates to Currency list
     */
    fun toCurrencyList(rates: Map<String, Double>): List<Currency> {
      return rates.map {
        toCurrency(it.key, it.value)
      }
    }
  }

}

data class Currencies(
  var list: List<Currency>,
  val sourceType: SourceType
)

/**
 * Type of LiveData sources that trigger the refresh of the currency list
 */
enum class SourceType {
  /**
   * The list items are the same but sorted differently
   */
  SORTING,
  /**
   * The list items don't have the same content
   */
  RATE_VALUES
}

/**
 * Currency list sorting order
 */
enum class Order {
  /**
   * List is sorted by ascending rate value
   */
  ASCENDING_RATE,
  /**
   * List is sorted by descending rate value
   */
  DESCENDING_RATE,
  /**
   * List is sorted by name
   */
  NAME,
  /**
   * List is not sorted
   */
  DEFAULT
}