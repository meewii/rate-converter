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
  fun `get rates from DB with up-to-date data`() {
    // having
    val now = LocalDateTime.of(2019, 3, 5, 17, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock)

    val testObserver = sut.getDbRates("GBP").test()
    testObserver.awaitTerminalEvent()

    // then
    testObserver.assertValue(successRateList)
  }

  @Test
  fun `get rates from DB before 16`() {
    // having
    val now = LocalDateTime.of(2019, 3, 6, 12, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock)

    // when
    val testObserver = sut.getDbRates("GBP").test()
    testObserver.awaitTerminalEvent()

    // then
    testObserver.assertValue(successRateList)
  }


  @Test
  fun `get rates from DB after 16`() {
    // having
    val now = LocalDateTime.of(2019, 3, 6, 16, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock)

    // when
    val testObserver = sut.getDbRates("GBP").test()
    testObserver.awaitTerminalEvent()

    // then
    testObserver.assertComplete()
  }

  @Test
  fun `get rates from DB with outdated data`() {
    // having
    val now = LocalDateTime.of(2019, 3, 7, 17, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock)

    // when
    val testObserver = sut.getDbRates("GBP").test()
    testObserver.awaitTerminalEvent()

    // then
    testObserver.assertComplete()
  }

  @Test
  fun `get rates from API`() {
    // having
    val now = LocalDateTime.of(2019, 3, 7, 17, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock)
    whenever(serviceMock.getRatesForBase(any())).thenReturn(Single.just(apiResponse))

    // when
    val testObserver = sut.getApiRates("GBP").test()
    testObserver.awaitTerminalEvent()

    // then
    verify(daoMock).insert(any())
    testObserver.assertValue(successRateList)
  }

  @Test
  fun `get rates from API with AUD base currency`() {
    // having
    val now = LocalDateTime.of(2019, 3, 7, 17, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock)
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
  fun `get rates API error`() {
    // having
    val now = LocalDateTime.of(2019, 3, 7, 17, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock)
    whenever(serviceMock.getRatesForBase("AUD")).thenReturn(Single.error(TestException("Boo")))

    // when
    val testObserver = sut.getApiRates("AUD").test()
    testObserver.awaitTerminalEvent()

    // then
    testObserver.assertError(TestException("Boo"))
  }

  @Test
  fun `get updated data with different user input`() {
    // having
    val now = LocalDateTime.of(2019, 3, 6, 12, 0)
    clockMock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
    sut = ExchangeRateRepository(serviceMock, daoMock, clockMock, userInputRepositoryMock)

    whenever(serviceMock.getRatesForBase(any())).thenReturn(Single.just(apiResponse))
    whenever(userInputRepositoryMock.getUserInputsStream()).thenReturn(Flowable.just(2.0))

    // when
    val testSubscriber = sut.getCombinedRates("GBP").test()
    testSubscriber.awaitTerminalEvent()

    // then
    testSubscriber.assertValue { (it as RateList.Success).currencies[0].calculatedValue == 6.0 }
    testSubscriber.assertValue { (it as RateList.Success).currencies[1].calculatedValue == 8.0 }
    testSubscriber.assertValue { (it as RateList.Success).currencies[2].calculatedValue == 4.0 }
  }

}

data class TestException(val msg: String) : Exception(msg)