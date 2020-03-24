package com.meewii.rateconverter.business

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.meewii.rateconverter.business.preferences.UserPreferences
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE, sdk = [28])
class UserInputRepositoryTest {

  private lateinit var sut: UserInputRepository
  @MockK(relaxed = true) lateinit var userPreferencesMock: UserPreferences

  @Before
  fun setup() {
    MockKAnnotations.init(this)

    every { userPreferencesMock.getLastUserInput() } returns 5.0
    sut = UserInputRepository(userPreferencesMock)
  }

  @After
  fun breakdown() {
    clearAllMocks()
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