package com.meewii.rateconverter.business

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.meewii.rateconverter.R
import com.meewii.rateconverter.business.database.ExchangeRateDao
import com.meewii.rateconverter.business.database.ExchangeRateEntity
import com.meewii.rateconverter.business.network.ExchangeRateService
import com.meewii.rateconverter.ui.Currency
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.annotation.Config
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE, sdk = [28])
class ExchangeRateRepositoryTest {

  @get:Rule
  val rule: MockitoRule = MockitoJUnit.rule()

  private lateinit var sut: ExchangeRateRepository
  @Mock lateinit var serviceMock: ExchangeRateService
  @Mock lateinit var userInputRepositoryMock: UserInputRepository
  @Mock lateinit var pinedCurrenciesRepositoryMock: PinedCurrenciesRepository
  @Mock lateinit var daoMock: ExchangeRateDao

  private val entity = ExchangeRateEntity(
    "GBP",
    LocalDateTime.of(2019, 3, 5, 16, 0),
    mapOf("CAD" to 6.0, "AED" to 2.0)
  )

  private val successDbRateList = RateList.Success("GBP",
    listOf(
      Currency(
        currencyCode = "CAD", rateValue = 6.0, flagResId = R.drawable.ic_flag_cad,
        nameResId = R.string.currency_name_CAD
      ),
      Currency(
        currencyCode = "AED", rateValue = 2.0, flagResId = R.drawable.ic_flag_aed,
        nameResId = R.string.currency_name_AED
      )
    )
  )

  private val successApiRateList = RateList.Success("GBP",
    listOf(
      Currency(
        currencyCode = "AUD", rateValue = 3.0, flagResId = R.drawable.ic_flag_aud,
        nameResId = R.string.currency_name_AUD
      ),
      Currency(
        currencyCode = "EUR", rateValue = 4.0, flagResId = R.drawable.ic_flag_eur,
        nameResId = R.string.currency_name_EUR
      ),
      Currency(
        currencyCode = "AED", rateValue = 2.0, flagResId = R.drawable.ic_flag_aed,
        nameResId = R.string.currency_name_AED
      )
    )
  )

  private val apiResponse = ExchangeRateService.ExchangeRateResponse(
    base = "GBP",
    date = LocalDate.of(2019, 3, 7),
    errorMessage = null,
    rates = mapOf("AUD" to 3.0, "EUR" to 4.0, "AED" to 2.0)
  )

  @Before
  fun setup() {
    sut = ExchangeRateRepository(serviceMock, daoMock, userInputRepositoryMock, pinedCurrenciesRepositoryMock)
  }

  @Test
  fun `get DB rates, with valid request, should return data as RateList`() {
    // having
    whenever(daoMock.getRatesForBase(any())).thenReturn(Single.just(entity))

    val testSubscriber = sut.getDbRates("GBP").test()
    testSubscriber.awaitTerminalEvent()

    // then
    testSubscriber.assertValue(successDbRateList)
  }

  @Test
  fun `get DB rates, with invalid request, should throw exception`() {
    // having
    whenever(daoMock.getRatesForBase(any())).thenReturn(Single.error(TestException("err")))

    // when
    val testSubscriber = sut.getDbRates("GBP").test()
    testSubscriber.awaitTerminalEvent()

    // then
    testSubscriber.assertError(TestException::class.java)
  }

  @Test
  fun `get API rates, successful response, should parse to RateList Success`() {
    // having
    whenever(serviceMock.getRatesForBase(any())).thenReturn(Single.just(apiResponse))

    // when
    val testSubscriber = sut.getApiRates("GBP").test()
    testSubscriber.awaitTerminalEvent()

    // then
    testSubscriber.assertValue(successApiRateList)
  }

  @Test
  fun `get API rates, invalid response, should throw an exception`() {
    // having
    whenever(serviceMock.getRatesForBase(any())).thenReturn(Single.just(ExchangeRateService.ExchangeRateResponse()))

    // when
    val testSubscriber = sut.getApiRates("GBP").test()
    testSubscriber.awaitTerminalEvent()

    // then
    testSubscriber.assertError(InvalidResponseException::class.java)
  }

  @Test
  fun `get API rates, error in response, should throw an exception`() {
    // having
    whenever(serviceMock.getRatesForBase(any()))
      .thenReturn(Single.just(ExchangeRateService.ExchangeRateResponse(errorMessage = "Some error")))

    // when
    val testSubscriber = sut.getApiRates("GBP").test()
    testSubscriber.awaitTerminalEvent()

    // then
    testSubscriber.assertError(ResponseErrorException::class.java)
  }

  @Test
  fun `get API rates, error response, should forward error`() {
    // having
    whenever(serviceMock.getRatesForBase(any())).thenReturn(Single.error(TestException("error")))

    // when
    val testSubscriber = sut.getApiRates("GBP").test()
    testSubscriber.awaitTerminalEvent()

    // then
    testSubscriber.assertError(TestException::class.java)
  }

  @Test
  fun `get API rates, with AUD base currency, AUD should be excluded from response`() {
    // having
    val apiResponse = ExchangeRateService.ExchangeRateResponse(
      base = "AUD",
      date = LocalDate.of(2019, 3, 7),
      errorMessage = null,
      rates = mapOf("AUD" to 3.0, "EUR" to 4.0, "AED" to 2.0)
    )
    whenever(serviceMock.getRatesForBase("AUD")).thenReturn(Single.just(apiResponse))

    // when
    val testSubscriber = sut.getApiRates("AUD").test()
    testSubscriber.awaitTerminalEvent()

    // then
    testSubscriber.assertValue(
      RateList.Success("AUD",
        listOf(
          Currency(
            currencyCode = "EUR", rateValue = 4.0, flagResId = R.drawable.ic_flag_eur,
            nameResId = R.string.currency_name_EUR
          ),
          Currency(
            currencyCode = "AED", rateValue = 2.0, flagResId = R.drawable.ic_flag_aed,
            nameResId = R.string.currency_name_AED
          )
        )
      )
    )
  }

  @Test
  fun `get combined rates, with user input 2, should multiply rates by 2`() {
    // having
    whenever(serviceMock.getRatesForBase(any())).thenReturn(Single.just(apiResponse))
    whenever(daoMock.getRatesForBase(any())).thenReturn(Single.just(entity))
    whenever(userInputRepositoryMock.getUserInputsStream()).thenReturn(Flowable.just(2.0))
    whenever(pinedCurrenciesRepositoryMock.getPinedCurrencies()).thenReturn(Flowable.just(setOf()))

    // when
    val testSubscriber = sut.getCombinedRates("GBP").test()
    testSubscriber.awaitTerminalEvent()

    // then
    testSubscriber.assertValue { (it as RateList.Success).currencies[0].calculatedValue == 6.0 }
    testSubscriber.assertValue { (it as RateList.Success).currencies[1].calculatedValue == 8.0 }
    testSubscriber.assertValue { (it as RateList.Success).currencies[2].calculatedValue == 4.0 }
  }

  @Test
  fun `get combined rates, error from API, success from DB, should return DB data`() {
    // having
    whenever(serviceMock.getRatesForBase(any())).thenReturn(Single.error(TestException("error")))
    whenever(daoMock.getRatesForBase(any())).thenReturn(Single.just(entity))
    whenever(userInputRepositoryMock.getUserInputsStream()).thenReturn(Flowable.just(1.0))
    whenever(pinedCurrenciesRepositoryMock.getPinedCurrencies()).thenReturn(Flowable.just(setOf()))

    // when
    val testSubscriber = sut.getCombinedRates("GBP").test()
    testSubscriber.awaitTerminalEvent()

    // then
    testSubscriber.assertValue(successDbRateList)
  }

  @Test
  fun `get combined rates, error from API and DB, should throw error`() {
    // having
    whenever(serviceMock.getRatesForBase(any())).thenReturn(Single.error(ResponseErrorException("error")))
    whenever(daoMock.getRatesForBase(any())).thenReturn(Single.error(TestException("error")))
    whenever(userInputRepositoryMock.getUserInputsStream()).thenReturn(Flowable.just(1.0))
    whenever(pinedCurrenciesRepositoryMock.getPinedCurrencies()).thenReturn(Flowable.just(setOf()))

    // when
    val testSubscriber = sut.getCombinedRates("GBP").test()
    testSubscriber.awaitTerminalEvent()

    // then
    testSubscriber.assertError(TestException::class.java)
  }

}

data class TestException(val msg: String) : Exception(msg)