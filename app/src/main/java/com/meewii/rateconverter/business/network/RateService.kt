package com.meewii.rateconverter.business.network

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface to perform Revolut's API requests
 */
interface RateService {

  @GET("/latest")
  fun getRatesForBase(
    @Query("base") base: String = "EUR"
  ): Single<Response>

  data class Response(
    @SerializedName("base") val base: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("rates") val rates: Map<String, Double>? = null,
    @SerializedName("error") val errorMessage: String? = null
  )

}