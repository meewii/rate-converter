package com.meewii.rateconverter.business

import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.meewii.rateconverter.R
import com.meewii.rateconverter.business.network.RateService
import com.meewii.rateconverter.ui.Rate
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@RunWith(AndroidJUnit4::class)
class PollRateManagerTest {

  @get:Rule
  val rule: MockitoRule = MockitoJUnit.rule()

  private lateinit var sut: PollRateManager
  @Mock lateinit var rateService: RateService

  private val successRateResponse = RateResponse.Success(listOf(Rate(
      currencyCode = "AUD", rateValue = 3.0, flagResId = R.drawable.ic_flag_aud,
      nameResId = R.string.currency_name_aud)))

  private val successRateResponseLiveData
      = MutableLiveData<RateResponse>().apply { value = successRateResponse }

  @Before
  fun setup() {
    sut = PollRateManager(rateService)
  }

  @Test
  fun `start polling rates`() {
    // having
    val response = RateService.Response(base = "EUR", date = "", errorMessage = null,
        rates = mapOf("AUD" to 3.0)
    )
    whenever(rateService.getRatesForBase(any())).thenReturn(Single.just(response))

    // when
    sut.startPollingRates("GBP")

    // then
    Truth.assertThat(sut.cachedBaseCurrency).isEqualTo("GBP")
    Truth.assertThat(sut.rateResponse.value).isEqualTo(successRateResponseLiveData.value)
  }

}