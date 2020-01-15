package com.meewii.rateconverter.core

import com.meewii.rateconverter.R

/**
 * Returns the flag's drawable ID of matching currency code
 */
fun flagMapper(currencyCode: String): Int {
  // TODO add all the other flags. For demo purposes only 4 are added here
  val flags = mutableMapOf(
    "EUR" to R.drawable.ic_flag_eur,
    "AUD" to R.drawable.ic_flag_aud,
    "GBP" to R.drawable.ic_flag_gbp,
    "USD" to R.drawable.ic_flag_usa
  )
  return flags[currencyCode] ?: R.drawable.ic_broken_image_24
}

/**
 * Returns the name's string ID of matching currency code
 */
fun nameMapper(currencyCode: String): Int {
  // TODO add all the other names. For demo purposes only 4 are added here. 
  // Maybe talk with BE dev to add them to the API response :)
  val flags = mutableMapOf(
    "EUR" to R.string.currency_name_eur,
    "AUD" to R.string.currency_name_aud,
    "GBP" to R.string.currency_name_gbp,
    "USD" to R.string.currency_name_usa
  )
  return flags[currencyCode] ?: R.string.currency_name_not_found
}

