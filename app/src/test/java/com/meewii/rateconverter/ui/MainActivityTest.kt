package com.meewii.rateconverter.ui

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.meewii.rateconverter.R
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.android.synthetic.main.activity_main.ui_rate_input
import kotlinx.android.synthetic.main.activity_main.ui_recycler_view
import kotlinx.android.synthetic.main.activity_main.ui_swipe_container
import kotlinx.android.synthetic.main.li_rate_display.view.ui_currency_code
import kotlinx.android.synthetic.main.li_rate_display.view.ui_currency_name
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = MainActivityTest.TestApp::class, sdk = [28])
class MainActivityTest {

  @get:Rule val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

  private lateinit var mainViewModel: MainViewModel
  private lateinit var viewStatusLiveData: MutableLiveData<ViewStatus>
  private lateinit var sortedRatesLiveData: MutableLiveData<Currencies>
  private lateinit var baseCurrencyLiveData: MutableLiveData<Currency>
  private lateinit var lastUserInputLiveData: MutableLiveData<Double>

  private lateinit var activity: MainActivity
  private lateinit var activityController: ActivityController<MainActivity>

  private val currencies = listOf(
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

  private val baseCurrency = Currency(
    currencyCode = "DKK", rateValue = 2.0, flagResId = R.drawable.ic_flag_dkk,
    nameResId = R.string.currency_name_DKK
  )

  @Before
  fun setUp() {
    activityController = Robolectric.buildActivity(MainActivity::class.java)
    activity = activityController.get()
    activityController.setup()

    activityScenarioRule.scenario.onActivity {
      mainViewModel = activity.mainViewModel
      viewStatusLiveData = mainViewModel.viewStatus as MutableLiveData<ViewStatus>
      sortedRatesLiveData = mainViewModel.sortedRates as MutableLiveData<Currencies>
      baseCurrencyLiveData = mainViewModel.baseCurrency as MutableLiveData<Currency>
      lastUserInputLiveData = mainViewModel.lastUserInput as MutableLiveData<Double>
    }
  }

  @After
  fun breakdown() {
    clearAllMocks()
    activityScenarioRule.scenario.close()
  }

  @Test
  fun `displays list items`() {
    sortedRatesLiveData.value = Currencies(currencies, SourceType.RATE_VALUES)

    Truth.assertThat((activity.ui_recycler_view.adapter as? CurrencyListAdapter)?.data).isEqualTo(currencies)
  }

  @Test
  fun `displays base currency`() {
    baseCurrencyLiveData.value = baseCurrency

    Truth.assertThat(activity.ui_rate_input.ui_currency_code.text).isEqualTo("DKK")
    Truth.assertThat(activity.ui_rate_input.ui_currency_name.text).isEqualTo("Danish Krone")
  }

  @Test
  fun `displays loading wheel`() {
    viewStatusLiveData.value = ViewStatus.Loading

    Truth.assertThat(activity.ui_swipe_container.isRefreshing).isTrue()
  }

  @Test
  fun `displays error snackbar`() {
    baseCurrencyLiveData.value = baseCurrency
    viewStatusLiveData.value = ViewStatus.Error("oh la la")

    Truth.assertThat(activity.ui_swipe_container.isRefreshing).isFalse()
    Truth.assertThat(activity.snackBar?.isShown).isTrue()
  }

  class TestApp : Application(), HasAndroidInjector {

    override fun setTheme(resid: Int) {
      super.setTheme(R.style.AppTheme)
    }

    private val viewModel = mockk<MainViewModel> {
      every { viewStatus } returns MutableLiveData()
      every { sortedRates } returns MutableLiveData()
      every { baseCurrency } returns MutableLiveData()
      every { lastUserInput } returns MutableLiveData()
      every { subscribeToRates(any()) } returns Unit
    }

    override fun androidInjector(): AndroidInjector<Any> {
      return AndroidInjector {
        val activity = it as MainActivity
        activity.mainViewModel = viewModel
      }
    }
  }
}