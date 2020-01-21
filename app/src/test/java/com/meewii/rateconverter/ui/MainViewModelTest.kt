package com.meewii.rateconverter.ui

import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.jraska.livedata.test
import com.meewii.rateconverter.R
import com.meewii.rateconverter.business.PollRateManager
import com.meewii.rateconverter.business.RateResponse
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
class MainViewModelTest {
  @get:Rule val rule: MockitoRule = MockitoJUnit.rule()

  private lateinit var sut: MainViewModel
  @Mock lateinit var pollRateManager: PollRateManager

  private val successRateResponse = RateResponse.Success(
    listOf(
      Rate(
        currencyCode = "AUD", rateValue = 3.0, flagResId = R.drawable.ic_flag_aud,
        nameResId = R.string.currency_name_aud
      )
    )
  )

  @Before
  fun setup() {
    sut = MainViewModel(pollRateManager)

    val liveData = MutableLiveData<RateResponse>().apply { value = successRateResponse }
    whenever(pollRateManager.rateResponse).thenReturn(liveData)
  }

  @Test
  fun `subscribes to rates`() {
    // when
    sut.subscribeToRates("GBP")

    // then
    verify(pollRateManager).startPollingRates("GBP")
    Truth.assertThat(sut.cachedBaseCurrency).isEqualTo("GBP")
  }

  @Test
  fun `update view status`() {
    // Having
    val observer = sut.viewStatus.test()

    // When
    sut.subscribeToRates()

    // Then
    observer.assertHasValue()
    observer.assertValue(ViewStatus.Loading)
    observer.assertHistorySize(1)

    // When
    sut.combineLatestData(successRateResponse, 2.0)

    // Then
    observer.assertHasValue()
    observer.assertValue(ViewStatus.Idle)
    observer.assertHistorySize(2)

    // When
    sut.combineLatestData(RateResponse.Error(), 2.0)

    // Then
    observer.assertHasValue()
    observer.assertValue(ViewStatus.Error())
    observer.assertHistorySize(3)
  }

  @Test
  fun `combine latest data`() {
    // Having
    sut.initCombinedRates()
    val observer = sut.combinedRates.test()

    // When
    sut.newUserInput(2.0)

    // Then
    observer.assertHasValue()

    val expectedRate = Rate(
      currencyCode = "AUD", rateValue = 3.0, flagResId = R.drawable.ic_flag_aud,
      nameResId = R.string.currency_name_aud
    )
    observer.assertValue(listOf(expectedRate))
    observer.assertValue { it.first().calculatedValue == 6.0 }
  }
}

