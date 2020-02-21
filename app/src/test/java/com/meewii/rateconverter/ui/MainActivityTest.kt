package com.meewii.rateconverter.ui

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.common.truth.Truth
import com.meewii.rateconverter.R
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_main.ui_rate_input
import kotlinx.android.synthetic.main.activity_main.ui_recycler_view
import kotlinx.android.synthetic.main.activity_main.ui_swipe_container
import kotlinx.android.synthetic.main.inc_rate_input.view.ui_currency_code
import kotlinx.android.synthetic.main.inc_rate_input.view.ui_currency_name
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = MainActivityTest.TestApp::class, sdk = [28])
class MainActivityTest {

  @Rule
  @JvmField
  val mockitoRule: MockitoRule = MockitoJUnit.rule()

  private lateinit var activity: MainActivity
  private lateinit var activityController: ActivityController<MainActivity>

  @Mock lateinit var viewModel: MainViewModel

  @Mock private lateinit var viewStatusLiveData: MutableLiveData<ViewStatus>
  @Mock private lateinit var sortedRatesLiveData: MutableLiveData<Currencies>
  @Mock private lateinit var baseCurrencyLiveData: MutableLiveData<Currency>

  @Captor private lateinit var viewStatusObserverCaptor: ArgumentCaptor<Observer<ViewStatus>>
  @Captor private lateinit var sortedRatesObserverCaptor: ArgumentCaptor<Observer<Currencies>>
  @Captor private lateinit var baseCurrencyObserverCaptor: ArgumentCaptor<Observer<Currency>>

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
    activity.setTestViewModel(viewModel)

    whenever(viewModel.viewStatus).thenReturn(viewStatusLiveData)
    whenever(viewModel.sortedRates).thenReturn(sortedRatesLiveData)
    whenever(viewModel.baseCurrency).thenReturn(baseCurrencyLiveData)

    activityController.create()
    activityController.start()

    verify(viewStatusLiveData).observe(ArgumentMatchers.any(LifecycleOwner::class.java), viewStatusObserverCaptor.capture())
    verify(sortedRatesLiveData).observe(ArgumentMatchers.any(LifecycleOwner::class.java), sortedRatesObserverCaptor.capture())
    verify(baseCurrencyLiveData).observe(ArgumentMatchers.any(LifecycleOwner::class.java), baseCurrencyObserverCaptor.capture())
  }

  @Test
  fun `displays list items`() {
    sortedRatesObserverCaptor.value.onChanged(Currencies(currencies, SourceType.RATE_VALUES))

    Truth.assertThat((activity.ui_recycler_view.adapter as? CurrencyListAdapter)?.data).isEqualTo(currencies)
  }

  @Test
  fun `displays base currency`() {
    baseCurrencyObserverCaptor.value.onChanged(baseCurrency)

    Truth.assertThat(activity.ui_rate_input.ui_currency_code.text).isEqualTo("DKK")
    Truth.assertThat(activity.ui_rate_input.ui_currency_name.text).isEqualTo("Danish Krone")
  }

  @Test
  fun `displays loading wheel`() {
    viewStatusObserverCaptor.value.onChanged(ViewStatus.Loading)

    Truth.assertThat(activity.ui_swipe_container.isRefreshing).isTrue()
  }

  @Test
  fun `displays error snackbar`() {
    baseCurrencyObserverCaptor.value.onChanged(baseCurrency)
    viewStatusObserverCaptor.value.onChanged(ViewStatus.Error("oh la la"))

    Truth.assertThat(activity.ui_swipe_container.isRefreshing).isFalse()
    Truth.assertThat(activity.snackBar?.isShown).isTrue()
  }

  class TestApp : Application(), HasAndroidInjector {

    override fun setTheme(resid: Int) {
      super.setTheme(R.style.AppTheme)
    }

    override fun androidInjector(): AndroidInjector<Any> {
      return AndroidInjector {
        val activity = it as MainActivity
      }
    }
  }
}