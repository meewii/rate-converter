package com.meewii.rateconverter.core

import com.meewii.rateconverter.R

/**
 * Returns the flag's drawable ID of matching currency code
 */
fun flagMapper(currencyCode: String): Int {
  val flags = mutableMapOf(
    "AUD" to R.drawable.ic_broken_image_24,
    "BGN" to R.drawable.ic_broken_image_24,
    "BRL" to R.drawable.ic_broken_image_24,
    "CAD" to R.drawable.ic_broken_image_24,
    "CHF" to R.drawable.ic_broken_image_24,
    "CNY" to R.drawable.ic_broken_image_24,
    "CZK" to R.drawable.ic_broken_image_24,
    "DKK" to R.drawable.ic_broken_image_24,
    "GBP" to R.drawable.ic_broken_image_24,
    "HKD" to R.drawable.ic_broken_image_24,
    "HRK" to R.drawable.ic_broken_image_24,
    "IDR" to R.drawable.ic_broken_image_24,
    "ILS" to R.drawable.ic_broken_image_24,
    "INR" to R.drawable.ic_broken_image_24,
    "ISK" to R.drawable.ic_broken_image_24,
    "JPY" to R.drawable.ic_broken_image_24,
    "KRW" to R.drawable.ic_broken_image_24,
    "MXN" to R.drawable.ic_broken_image_24,
    "MYR" to R.drawable.ic_broken_image_24,
    "NOK" to R.drawable.ic_broken_image_24,
    "NZD" to R.drawable.ic_broken_image_24,
    "PHP" to R.drawable.ic_broken_image_24,
    "PLN" to R.drawable.ic_broken_image_24,
    "RON" to R.drawable.ic_broken_image_24,
    "RUB" to R.drawable.ic_broken_image_24,
    "SEK" to R.drawable.ic_broken_image_24,
    "SGD" to R.drawable.ic_broken_image_24,
    "THB" to R.drawable.ic_broken_image_24,
    "TRY" to R.drawable.ic_broken_image_24,
    "USD" to R.drawable.ic_broken_image_24,
    "ZAR" to R.drawable.ic_broken_image_24,
    "EUR" to R.drawable.ic_broken_image_24
  )
  return flags[currencyCode] ?: R.drawable.ic_broken_image_24
}


/**
 * Returns the name's string ID of matching currency code
 */
fun nameMapper(currencyCode: String): Int {
  val flags = mutableMapOf(
    "AUD" to R.string.currency_name_not_found,
    "BGN" to R.string.currency_name_not_found,
    "BRL" to R.string.currency_name_not_found,
    "CAD" to R.string.currency_name_not_found,
    "CHF" to R.string.currency_name_not_found,
    "CNY" to R.string.currency_name_not_found,
    "CZK" to R.string.currency_name_not_found,
    "DKK" to R.string.currency_name_not_found,
    "GBP" to R.string.currency_name_not_found,
    "HKD" to R.string.currency_name_not_found,
    "HRK" to R.string.currency_name_not_found,
    "IDR" to R.string.currency_name_not_found,
    "ILS" to R.string.currency_name_not_found,
    "INR" to R.string.currency_name_not_found,
    "ISK" to R.string.currency_name_not_found,
    "JPY" to R.string.currency_name_not_found,
    "KRW" to R.string.currency_name_not_found,
    "MXN" to R.string.currency_name_not_found,
    "MYR" to R.string.currency_name_not_found,
    "NOK" to R.string.currency_name_not_found,
    "NZD" to R.string.currency_name_not_found,
    "PHP" to R.string.currency_name_not_found,
    "PLN" to R.string.currency_name_not_found,
    "RON" to R.string.currency_name_not_found,
    "RUB" to R.string.currency_name_not_found,
    "SEK" to R.string.currency_name_not_found,
    "SGD" to R.string.currency_name_not_found,
    "THB" to R.string.currency_name_not_found,
    "TRY" to R.string.currency_name_not_found,
    "USD" to R.string.currency_name_not_found,
    "ZAR" to R.string.currency_name_not_found,
    "EUR" to R.string.currency_name_not_found
  )
  return flags[currencyCode] ?: R.string.currency_name_not_found
}

