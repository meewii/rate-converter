package com.meewii.rateconverter.business

import com.meewii.rateconverter.business.network.RateService
import com.meewii.rateconverter.core.flagMapper
import com.meewii.rateconverter.core.nameMapper
import com.meewii.rateconverter.ui.Rate
import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject

@Reusable
class RateRepository @Inject constructor(private val service: RateService) {

  fun getRatesForBase(base: String): Single<RateResponse> {
    return service.getRatesForBase(base)
      .map { response ->
        if (response.errorMessage != null) RateResponse.Error(null, response.errorMessage)
        else if (response.base == null || response.rates == null) {
          RateResponse.Error(null, "The API is not returning rates or base currency")
        } else {
          RateResponse.Success(toRateList(response.base, response.rates))
        }
      }
      .onErrorReturn { RateResponse.Error(it) }
  }

  private fun toRateList(base: String, rates: Map<String, Double>): List<Rate> {
    val mutableList = mutableListOf<Rate>()

    mutableList.add(
      Rate(position = 0, currencyCode = base, flagResId = flagMapper(base), nameResId = nameMapper(base))
    )

    rates.forEach {
      val rate = Rate(
        currencyCode = it.key, value = it.value, flagResId = flagMapper(it.key), nameResId = nameMapper(it.key)
      )
      mutableList.add(rate)
    }
    return mutableList
  }

}

sealed class RateResponse {
  data class Error(val error: Throwable? = null, val errorMessage: String? = null) : RateResponse()
  data class Success(val rates: List<Rate>) : RateResponse()
}