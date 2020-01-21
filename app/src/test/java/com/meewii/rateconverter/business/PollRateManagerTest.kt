package com.meewii.rateconverter.business

import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.meewii.rateconverter.R
import com.meewii.rateconverter.business.network.RateService
import com.meewii.rateconverter.ui.Rate
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE, sdk=[28])
class PollRateManagerTest {

  @get:Rule
  val rule: MockitoRule = MockitoJUnit.rule()

  private lateinit var sut: PollRateManager
  @Mock lateinit var rateService: RateService

  private val successRateResponse = RateResponse.Success(
    listOf(
      Rate(position = 0,
        currencyCode = "GBP", rateValue = 1.0, flagResId = R.drawable.ic_flag_gbp,
        nameResId = R.string.currency_name_gbp
      ),
      Rate(position = 1,
        currencyCode = "AUD", rateValue = 3.0, flagResId = R.drawable.ic_flag_aud,
        nameResId = R.string.currency_name_aud
      ),
      Rate(position = 1,
        currencyCode = "EUR", rateValue = 4.0, flagResId = R.drawable.ic_flag_eur,
        nameResId = R.string.currency_name_eur
      )
    )
  )

  private val successRateResponseLiveData =
    MutableLiveData<RateResponse>().apply { value = successRateResponse }

  @Before
  fun setup() {
    sut = PollRateManager(rateService)
  }

  @Test
  fun `get rates for base GBP`() {
    // having
    val testScheduler = TestScheduler()
    val testSubscriber = TestSubscriber<RateResponse>()
    val response = RateService.Response(
      base = "GBP", date = "", errorMessage = null,
      rates = mapOf("AUD" to 3.0, "EUR" to 4.0)
    )
    whenever(rateService.getRatesForBase(any())).thenReturn(Single.just(response))

    // when
    sut.getRatesForBase("GBP").observeOn(testScheduler).subscribe(testSubscriber)

    // then
    testScheduler.triggerActions()
    testSubscriber.assertValueCount(1)
    testSubscriber.assertValue(successRateResponse)
    testSubscriber.cancel()
  }

  @Test
  fun `error when getting`() {
    // having
    val testScheduler = TestScheduler()
    val testSubscriber = TestSubscriber<RateResponse>()
    val response = RateService.Response(
      base = null, date = "", errorMessage = null,
      rates = mapOf("AUD" to 3.0, "EUR" to 4.0)
    )
    whenever(rateService.getRatesForBase(any())).thenReturn(Single.just(response))

    // when
    sut.getRatesForBase("GBP").observeOn(testScheduler).subscribe(testSubscriber)

    // then
    testScheduler.triggerActions()
    testSubscriber.assertValueCount(1)
    testSubscriber.assertValue { it is RateResponse.Error }
    testSubscriber.cancel()
  }

}