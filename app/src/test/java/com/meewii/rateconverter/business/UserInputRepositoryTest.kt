package com.meewii.rateconverter.business

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE, sdk = [28])
class UserInputRepositoryTest {

  @get:Rule
  val rule: MockitoRule = MockitoJUnit.rule()

  private lateinit var sut: UserInputRepository

  @Before
  fun setup() {
    sut = UserInputRepository()
  }

  @Test
  fun `get default stream`() {
    // having
    val testSubscriber = TestSubscriber<Double>()

    val stream = sut.getUserInputsStream()

    // when
    stream.subscribe(testSubscriber)

    // then
    testSubscriber.assertValue(1.0)
  }

  @Test
  fun `get stream after user input`() {
    // having
    val testSubscriber = TestSubscriber<Double>()

    val stream = sut.getUserInputsStream()
    sut.setBaseRateValue(2.5)

    // when
    stream.subscribe(testSubscriber)

    // then
    testSubscriber.assertValue(2.5)
  }

}