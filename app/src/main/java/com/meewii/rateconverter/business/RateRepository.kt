package com.meewii.rateconverter.business

import com.meewii.rateconverter.business.network.RateService
import dagger.Reusable
import io.reactivex.Maybe
import javax.inject.Inject

@Reusable
class RateRepository @Inject constructor(private val service: RateService) {

  fun getRatesForBase(base: String)
      : Maybe<RateService.RateResponse> {
    return service.getRatesForBase(base)
  }
}