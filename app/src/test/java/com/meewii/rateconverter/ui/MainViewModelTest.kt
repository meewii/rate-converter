package com.meewii.rateconverter.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jraska.livedata.test
import com.meewii.rateconverter.R
import com.meewii.rateconverter.RxImmediateSchedulerRule
import com.meewii.rateconverter.business.ExchangeRateRepository
import com.meewii.rateconverter.business.RateList
import com.meewii.rateconverter.business.TestException
import com.meewii.rateconverter.business.UserInputRepository
import com.meewii.rateconverter.business.preferences.UserPreferences
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.After
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

  @get:Rule
  val rule: MockitoRule = MockitoJUnit.rule()

  @Rule
  @JvmField
  val instantExecutorRule = InstantTaskExecutorRule()

  @Rule
  @JvmField
  val rxImmediateSchedulerRule = RxImmediateSchedulerRule()

  private lateinit var sut: MainViewModel
  @Mock lateinit var exchangeRateRepositoryMock: ExchangeRateRepository
  @Mock lateinit var userInputRepositoryMock: UserInputRepository
  @Mock lateinit var userPreferencesMock: UserPreferences

  private val successRateList = RateList.Success(
    listOf(
      Currency(
        currencyCode = "AUD", rateValue = 4.0, flagResId = R.drawable.ic_flag_aud,
        nameResId = R.string.currency_name_AUD
      ),
      Currency(
        currencyCode = "EUR", rateValue = 3.0, flagResId = R.drawable.ic_flag_eur,
        nameResId = R.string.currency_name_EUR
      ),
      Currency(
        currencyCode = "AED", rateValue = 2.0, flagResId = R.drawable.ic_flag_aed,
        nameResId = R.string.currency_name_AED
      )
    )
  )

  val baseCurrencyEur = Currency(
    currencyCode = "EUR", rateValue = 1.0, flagResId = R.drawable.ic_flag_eur,
    nameResId = R.string.currency_name_EUR
  )

  @Before
  fun setup() {
    sut = MainViewModel(exchangeRateRepositoryMock, userInputRepositoryMock, userPreferencesMock)

    whenever(userPreferencesMock.getSortingOrder()).thenReturn(Order.DEFAULT)
  }

  @After
  fun breakdown() {
  }

  @Test
  fun `init VM with successful data and default base currency`() {
    // having
    whenever(exchangeRateRepositoryMock.getCombinedRates(any())).thenReturn(Flowable.just(successRateList))

    // when
    sut.subscribeToRates()

    val viewStatusObserver = sut.viewStatus.test()
    viewStatusObserver.assertValue(ViewStatus.Idle)

    val baseCurrencyObserver = sut.baseCurrency.test()
    baseCurrencyObserver.assertValue(baseCurrencyEur)

    val sortedRatesObserver = sut.sortedRates.test()
    sortedRatesObserver.assertValue(Currencies(successRateList.currencies, SourceType.RATE_VALUES))
  }

  @Test
  fun `force refresh API`() {
    // having
    whenever(exchangeRateRepositoryMock.getApiRates(any())).thenReturn(Single.just(successRateList))

    // when
    sut.forceRefreshRates()

    verify(exchangeRateRepositoryMock).getApiRates(any())
  }

  @Test
  fun `subscribe to AUD based rates`() {
    // having
    whenever(exchangeRateRepositoryMock.getCombinedRates(any())).thenReturn(Flowable.just(successRateList))

    // when
    sut.subscribeToRates("AUD")

    val baseCurrencyObserver = sut.baseCurrency.test()
    baseCurrencyObserver.assertValue(Currency(
      currencyCode = "AUD", rateValue = 1.0, flagResId = R.drawable.ic_flag_aud,
      nameResId = R.string.currency_name_AUD
    ))

    val sortedRatesObserver = sut.sortedRates.test()
    sortedRatesObserver.assertValue(Currencies(successRateList.currencies, SourceType.RATE_VALUES))
  }

  @Test
  fun `data sources return error`() {
    // having
    whenever(exchangeRateRepositoryMock.getCombinedRates(any())).thenReturn(Flowable.error(TestException("too bad")))

    // when
    sut.subscribeToRates("eduoaiud")

    val baseCurrencyObserver = sut.baseCurrency.test()
    baseCurrencyObserver.assertValue(baseCurrencyEur)

    val viewStatusObserver = sut.viewStatus.test()
    viewStatusObserver.assertValue(ViewStatus.Error(null, TestException("too bad")))
  }

  @Test
  fun `data sources return rate list error`() {
    // having
    whenever(exchangeRateRepositoryMock.getCombinedRates(any())).thenReturn(
      Flowable.just(
        RateList.Error(TestException("too bad"), "error")
      )
    )

    // when
    sut.subscribeToRates()

    val baseCurrencyObserver = sut.baseCurrency.test()
    baseCurrencyObserver.assertValue(baseCurrencyEur)

    val viewStatusObserver = sut.viewStatus.test()
    viewStatusObserver.assertValue(ViewStatus.Error("error", TestException("too bad")))
  }

  val unsortedList =
    listOf(
      Currency(
        currencyCode = "AED", rateValue = 2.0, flagResId = R.drawable.ic_flag_aed,
        nameResId = R.string.currency_name_AED
      ),
      Currency(
        currencyCode = "EUR", rateValue = 3.0, flagResId = R.drawable.ic_flag_eur,
        nameResId = R.string.currency_name_EUR
      ),
      Currency(
        currencyCode = "AUD", rateValue = 4.0, flagResId = R.drawable.ic_flag_aud,
        nameResId = R.string.currency_name_AUD
      )
    )

  @Test
  fun `sort list by ascendant rates`() {
    // having
    whenever(exchangeRateRepositoryMock.getCombinedRates(any())).thenReturn(Flowable.just(successRateList))
    sut.subscribeToRates()
    val sortedRatesObserver = sut.sortedRates.test()

    // when
    sut.setOrder(Order.ASCENDING_RATE)

    // then
    sortedRatesObserver.assertValue(Currencies(listOf(
      Currency(
        currencyCode = "AED", rateValue = 2.0, flagResId = R.drawable.ic_flag_aed,
        nameResId = R.string.currency_name_AED
      ),
      Currency(
        currencyCode = "EUR", rateValue = 3.0, flagResId = R.drawable.ic_flag_eur,
        nameResId = R.string.currency_name_EUR
      ),
      Currency(
        currencyCode = "AUD", rateValue = 4.0, flagResId = R.drawable.ic_flag_aud,
        nameResId = R.string.currency_name_AUD
      )
    ), SourceType.SORTING))

    // when
    sut.setOrder(Order.DESCENDING_RATE)

    // then
    sortedRatesObserver.assertValue(Currencies(listOf(
      Currency(
        currencyCode = "AUD", rateValue = 4.0, flagResId = R.drawable.ic_flag_aud,
        nameResId = R.string.currency_name_AUD
      ),
      Currency(
        currencyCode = "EUR", rateValue = 3.0, flagResId = R.drawable.ic_flag_eur,
        nameResId = R.string.currency_name_EUR
      ),
      Currency(
        currencyCode = "AED", rateValue = 2.0, flagResId = R.drawable.ic_flag_aed,
        nameResId = R.string.currency_name_AED
      )
    ), SourceType.SORTING))

    // when
    sut.setOrder(Order.NAME)

    // then
    sortedRatesObserver.assertValue(Currencies(listOf(
      Currency(
        currencyCode = "AED", rateValue = 2.0, flagResId = R.drawable.ic_flag_aed,
        nameResId = R.string.currency_name_AED
      ),
      Currency(
        currencyCode = "AUD", rateValue = 4.0, flagResId = R.drawable.ic_flag_aud,
        nameResId = R.string.currency_name_AUD
      ),
      Currency(
        currencyCode = "EUR", rateValue = 3.0, flagResId = R.drawable.ic_flag_eur,
        nameResId = R.string.currency_name_EUR
      )
    ), SourceType.SORTING))
  }
}

