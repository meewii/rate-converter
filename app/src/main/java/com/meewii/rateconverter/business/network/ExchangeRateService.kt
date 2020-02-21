package com.meewii.rateconverter.business.network

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import org.threeten.bp.LocalDate
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface to perform Exchange Rate's API requests
 */
interface ExchangeRateService {

  @GET("/latest")
  fun getRatesForBase(
    @Query("base") base: String = "EUR"
  ): Single<ExchangeRateResponse>

  data class ExchangeRateResponse(
    @SerializedName("base") val base: String? = null,
    @SerializedName("date") val date: LocalDate? = null,
    @SerializedName("rates") val rates: Map<String, Double>? = null,
    @SerializedName("error") val errorMessage: String? = null
  )

}