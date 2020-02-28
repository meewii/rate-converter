package com.meewii.rateconverter.business

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.meewii.rateconverter.R
import com.meewii.rateconverter.business.database.ExchangeRateDao
import com.meewii.rateconverter.business.database.ExchangeRateEntity
import com.meewii.rateconverter.business.network.ExchangeRateService
import com.meewii.rateconverter.ui.Currency
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.annotation.Config
import org.threeten.bp.Clock
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

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
  @Mock lateinit var clockMock: Clock

  private val successRateList = RateList.Success(
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
    val entity = ExchangeRateEntity(
      "GBP",
      LocalDateTime.of(2019, 3, 5, 16, 0),
      mapOf("AUD" to 3.0, "EUR" to 4.0, "AED" to 2.0)
    )
    whenever(daoMock.getRatesForBase(any())).thenReturn(Maybe.just(entity))
  }

  @Test
  fun `get DB rates, the same day than now, after 16, should return data`() {
    // having
    val now = LocalDateTime.of(2019, 3, 5, 17, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock, pinedCurrenciesRepositoryMock)

    val testObserver = sut.getDbRates("GBP").test()
    testObserver.awaitTerminalEvent()

    // then
    testObserver.assertValue(successRateList)
  }

  @Test
  fun `get DB rates, the day after now, before 16, should return data`() {
    // having
    val now = LocalDateTime.of(2019, 3, 6, 11, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock, pinedCurrenciesRepositoryMock)

    val testObserver = sut.getDbRates("GBP").test()
    testObserver.awaitTerminalEvent()

    // then
    testObserver.assertValue(successRateList)
  }

  @Test
  fun `get DB rates, the day after now, after 16, should return nothing`() {
    // having
    val now = LocalDateTime.of(2019, 3, 6, 16, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock, pinedCurrenciesRepositoryMock)

    // when
    val testObserver = sut.getDbRates("GBP").test()
    testObserver.awaitTerminalEvent()

    // then
    testObserver.assertComplete()
  }

  @Test
  fun `get DB rates, several days after now, should return nothing`() {
    // having
    val now = LocalDateTime.of(2019, 3, 7, 17, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock, pinedCurrenciesRepositoryMock)

    // when
    val testObserver = sut.getDbRates("GBP").test()
    testObserver.awaitTerminalEvent()

    // then
    testObserver.assertComplete()
  }

  @Test
  fun `get DB rates, several days before now, should return data`() {
    // having
    val now = LocalDateTime.of(2019, 3, 4, 17, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock, pinedCurrenciesRepositoryMock)

    // when
    val testObserver = sut.getDbRates("GBP").test()
    testObserver.awaitTerminalEvent()

    // then
    testObserver.assertValue(successRateList)
  }

  @Test
  fun `get API rates, successful response, should parse to RateList Success`() {
    // having
    val now = LocalDateTime.of(2019, 3, 7, 17, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock, pinedCurrenciesRepositoryMock)
    whenever(serviceMock.getRatesForBase(any())).thenReturn(Single.just(apiResponse))

    // when
    val testObserver = sut.getApiRates("GBP").test()
    testObserver.awaitTerminalEvent()

    // then
    verify(daoMock).insert(any())
    testObserver.assertValue(successRateList)
  }

  @Test
  fun `get API rates, invalid response, should parse to RateList Error`() {
    // having
    val now = LocalDateTime.of(2019, 3, 7, 17, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock, pinedCurrenciesRepositoryMock)
    whenever(serviceMock.getRatesForBase(any())).thenReturn(Single.just(ExchangeRateService.ExchangeRateResponse()))

    // when
    val testObserver = sut.getApiRates("GBP").test()
    testObserver.awaitTerminalEvent()

    // then
    testObserver.assertValue { it is RateList.Error }
  }

  @Test
  fun `get API rates, error response, should forward error`() {
    // having
    val now = LocalDateTime.of(2019, 3, 7, 17, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock, pinedCurrenciesRepositoryMock)
    whenever(serviceMock.getRatesForBase(any())).thenReturn(Single.error(TestException("error")))

    // when
    val testObserver = sut.getApiRates("GBP").test()
    testObserver.awaitTerminalEvent()

    // then
    testObserver.assertError(TestException("error"))
  }

  @Test
  fun `get API rates, with AUD base currency, AUD should be excluded from response`() {
    // having
    val now = LocalDateTime.of(2019, 3, 7, 17, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock, pinedCurrenciesRepositoryMock)
    whenever(serviceMock.getRatesForBase("AUD")).thenReturn(Single.just(apiResponse))

    // when
    val testObserver = sut.getApiRates("AUD").test()
    testObserver.awaitTerminalEvent()

    // then
    testObserver.assertValue(RateList.Success(
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
    ))
  }

  @Test
  fun `get combined API rates, with some pined currencies, should set corresponding pinned value to true`() {
    // having
    val now = LocalDateTime.of(2019, 3, 7, 17, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock, pinedCurrenciesRepositoryMock)

    whenever(serviceMock.getRatesForBase("AUD")).thenReturn(Single.just(apiResponse))
    whenever(userInputRepositoryMock.getUserInputsStream()).thenReturn(Flowable.just(1.0))
    whenever(pinedCurrenciesRepositoryMock.getPinedCurrencies()).thenReturn(Flowable.just(setOf("EUR", "PLN")))

    // when
    val testObserver = sut.getApiCombinedRates("AUD").test()
    testObserver.awaitTerminalEvent()

    // then
    val rateList = RateList.Success(
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
    testObserver.assertValue(rateList)
    testObserver.assertValue { (it as RateList.Success).currencies[0].isPinned }
    testObserver.assertValue { (it as RateList.Success).currencies[1].isPinned.not() }
  }

  @Test
  fun `get combined rates, with user input 2, should multiply rates by 2`() {
    // having
    val now = LocalDateTime.of(2019, 3, 6, 12, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock, pinedCurrenciesRepositoryMock)

    whenever(serviceMock.getRatesForBase(any())).thenReturn(Single.just(apiResponse))
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

  //TODO change behavior of this case
  @Test
  fun `get combined rates, error from API, after 16`() {
    // having
    val now = LocalDateTime.of(2019, 3, 7, 17, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock, pinedCurrenciesRepositoryMock)

    whenever(serviceMock.getRatesForBase(any())).thenReturn(Single.error(TestException("error")))
    whenever(userInputRepositoryMock.getUserInputsStream()).thenReturn(Flowable.just(1.0))
    whenever(pinedCurrenciesRepositoryMock.getPinedCurrencies()).thenReturn(Flowable.just(setOf()))

    // when
    val testSubscriber = sut.getCombinedRates("GBP").test()
    testSubscriber.awaitTerminalEvent()

    // then
    testSubscriber.assertError(TestException("error"))
  }

}

data class TestException(val msg: String) : Exception(msg)