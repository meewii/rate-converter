package com.meewii.rateconverter.ui

data class Rate(
  val currencyCode: String,
  var rateValue: Double = 1.0,
  val position: Int = 1,
  val flagResId: Int,
  val nameResId: Int
) {

  var calculatedValue: Double = rateValue

}
