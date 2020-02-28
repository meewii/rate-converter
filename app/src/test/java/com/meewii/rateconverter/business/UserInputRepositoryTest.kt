package com.meewii.rateconverter.business

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.meewii.rateconverter.business.preferences.UserPreferences
import com.nhaarman.mockitokotlin2.whenever
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
@Config(manifest = Config.NONE, sdk = [28])
class UserInputRepositoryTest {

  @get:Rule
  val rule: MockitoRule = MockitoJUnit.rule()

  private lateinit var sut: UserInputRepository
  @Mock lateinit var userPreferencesMock: UserPreferences

  @Before
  fun setup() {
    whenever(userPreferencesMock.getLastUserInput()).thenReturn(5.0)
    sut = UserInputRepository(userPreferencesMock)
  }

  @Test
  fun `get default stream`() {
    // having
    val testSubscriber = TestSubscriber<Double>()

    val stream = sut.getUserInputsStream()

    // when
    stream.subscribe(testSubscriber)

    // then
    testSubscriber.assertValue(5.0)
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