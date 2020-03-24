package com.meewii.rateconverter.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jraska.livedata.test
import com.meewii.rateconverter.R
import com.meewii.rateconverter.RxImmediateSchedulerRule
import com.meewii.rateconverter.business.ExchangeRateRepository
import com.meewii.rateconverter.business.PinedCurrenciesRepository
import com.meewii.rateconverter.business.RateList
import com.meewii.rateconverter.business.TestException
import com.meewii.rateconverter.business.UserInputRepository
import com.meewii.rateconverter.business.preferences.UserPreferences
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.reactivex.Flowable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE, sdk = [28])
@RunWith(AndroidJUnit4::class)
class MainViewModelTest {

  @Rule
  @JvmField
  val instantExecutorRule = InstantTaskExecutorRule()

  @Rule
  @JvmField
  val rxImmediateSchedulerRule = RxImmediateSchedulerRule()

  private lateinit var sut: MainViewModel
  @MockK lateinit var exchangeRateRepositoryMock: ExchangeRateRepository
  @MockK lateinit var userInputRepositoryMock: UserInputRepository
  @MockK lateinit var pinedCurrenciesRepositoryMock: PinedCurrenciesRepository
  @MockK(relaxed = true) lateinit var userPreferencesMock: UserPreferences

  private val successRateList = RateList("EUR",
    listOf(
      Currency(
        currencyCode = "AUD", rateValue = 4.0, flagResId = R.drawable.ic_flag_aud,
        nameResId = R.string.currency_name_AUD
      ).apply { isPinned = false },
      Currency(
        currencyCode = "EUR", rateValue = 3.0, flagResId = R.drawable.ic_flag_eur,
        nameResId = R.string.currency_name_EUR
      ).apply { isPinned = false },
      Currency(
        currencyCode = "AED", rateValue = 2.0, flagResId = R.drawable.ic_flag_aed,
        nameResId = R.string.currency_name_AED
      ).apply { isPinned = false }
    )
  )

  private val baseCurrencyEur = Currency(
    currencyCode = "EUR", rateValue = 1.0, flagResId = R.drawable.ic_flag_eur,
    nameResId = R.string.currency_name_EUR
  )

  @Before
  fun setup() {
    MockKAnnotations.init(this)

    sut = MainViewModel(exchangeRateRepositoryMock, userInputRepositoryMock,
      pinedCurrenciesRepositoryMock, userPreferencesMock)

    every { userPreferencesMock.getSortingOrder() } returns Order.DEFAULT
    every { userPreferencesMock.getBaseCurrency() } returns "EUR"
    every { userPreferencesMock.getLastUserInput() } returns 5.0
  }

  @After
  fun breakdown() {
    clearAllMocks()
  }

  @Test
  fun `init VM with successful data and default base currency`() {
    // having
    every { exchangeRateRepositoryMock.getCombinedRates(any()) } returns Flowable.just(successRateList)

    // when
    sut.subscribeToRates()

//    val viewStatusObserver = sut.viewStatus.test()
//    viewStatusObserver.assertValue(ViewStatus.Idle)

    val baseCurrencyObserver = sut.baseCurrency.test()
    baseCurrencyObserver.assertValue(baseCurrencyEur)

    val sortedRatesObserver = sut.sortedRates.test()
    sortedRatesObserver.assertValue(Currencies(successRateList.currencies, SourceType.RATE_VALUES))
  }

  @Test
  fun `force refresh API`() {
    // having
    every { exchangeRateRepositoryMock.getCombinedRates(any()) } returns Flowable.just(successRateList)

    // when
    sut.forceRefreshRates()

    verify { exchangeRateRepositoryMock.getCombinedRates(any()) }
  }

  @Test
  fun `subscribe to AUD based rates`() {
    // having
    every { exchangeRateRepositoryMock.getCombinedRates(any()) } returns Flowable.just(successRateList)

    // when
    sut.subscribeToRates("AUD")

    val sortedRatesObserver = sut.sortedRates.test()
    sortedRatesObserver.assertValue(Currencies(successRateList.currencies, SourceType.RATE_VALUES))
  }

  @Test
  fun `data sources return error`() {
    // having
    every { exchangeRateRepositoryMock.getCombinedRates(any()) } returns Flowable.error(TestException("too bad"))

    // when
    sut.subscribeToRates("eduoaiud")

    val baseCurrencyObserver = sut.baseCurrency.test()
    baseCurrencyObserver.assertValue(baseCurrencyEur)

    val viewStatusObserver = sut.viewStatus.test()
    viewStatusObserver.assertValue(ViewStatus.Error(null, TestException("too bad")))
  }

  @Test
  fun `sort list by ascendant rates`() {
    // having
    every { exchangeRateRepositoryMock.getCombinedRates(any()) } returns Flowable.just(successRateList)
    sut.subscribeToRates()
    val sortedRatesObserver = sut.sortedRates.test()

    // when
    sut.setOrder(Order.ASCENDING_RATE)

    // then
    verify { userPreferencesMock.setSortingOrder(Order.ASCENDING_RATE) }

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
    verify { userPreferencesMock.setSortingOrder(Order.DESCENDING_RATE) }

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
    verify { userPreferencesMock.setSortingOrder(Order.NAME) }

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

