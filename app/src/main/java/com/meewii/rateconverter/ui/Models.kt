package com.meewii.rateconverter.ui

data class Rate(
  val currencyCode: String,
  val value: Double = 1.0,
  val position: Int = 1,
  val flagResId: Int,
  val nameResId: Int
) {

  fun calculatedValue(userInput: Double) = userInput * value

}
