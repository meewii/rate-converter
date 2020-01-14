package com.meewii.rateconverter.business.network

import com.google.gson.annotations.SerializedName
import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface to perform Revolut's API requests
 */
interface RateService {

  @GET("/latest")
  fun getRatesForBase(
    @Query("base") base: String = "EUR"
  ): Maybe<RateResponse>

  data class RateResponse(
    @SerializedName("base") val base: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("rates") val rates: Map<String, Double>? = null,
    @SerializedName("error") val errorMessage: String? = null
  )

  /**
  {
  "base": "HUF",
  "date": "2018-09-06",
  "rates": {
    "AUD": 0.0049279,
    "BGN": 0.0059628,
    "BRL": 0.014609,
    "CAD": 0.0046761,
    "CHF": 0.0034375,
    "CNY": 0.024222,
    "CZK": 0.078397,
    "DKK": 0.022733,
    "GBP": 0.0027385,
    "HKD": 0.027842,
    "HRK": 0.022664,
    "IDR": 52.813,
    "ILS": 0.012715,
    "INR": 0.25523,
    "ISK": 0.38963,
    "JPY": 0.39496,
    "KRW": 3.9778,
    "MXN": 0.068186,
    "MYR": 0.01467,
    "NOK": 0.029804,
    "NZD": 0.0053757,
    "PHP": 0.19082,
    "PLN": 0.013165,
    "RON": 0.014141,
    "RUB": 0.2426,
    "SEK": 0.032289,
    "SGD": 0.0048779,
    "THB": 0.11625,
    "TRY": 0.023256,
    "USD": 0.0035468,
    "ZAR": 0.054338,
    "EUR": 0.0030487
    }
  }
   */

}