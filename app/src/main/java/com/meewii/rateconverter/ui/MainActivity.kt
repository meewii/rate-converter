package com.meewii.rateconverter.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.meewii.rateconverter.R
import com.meewii.rateconverter.business.InvalidResponseException
import com.meewii.rateconverter.business.TooManyAttemptsException
import com.meewii.rateconverter.core.gone
import com.meewii.rateconverter.core.visible
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.ui_error_message
import kotlinx.android.synthetic.main.activity_main.ui_recycler_view
import kotlinx.android.synthetic.main.activity_main.ui_swipe_container
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var mainViewModel: MainViewModel

  private val viewAdapter: MainListAdapter = MainListAdapter(::onClickItem, ::onUserInput)

  private fun onClickItem(rate: Rate) {
    mainViewModel.subscribeToRates(rate.currencyCode)
  }

  private fun onUserInput(value: Double) {
    mainViewModel.newUserInput(value)
  }

  private val rateListObserver = Observer<List<Rate>> { rates ->
    Timber.d("Rates? ${rates.size}")
    rates?.let {
      viewAdapter.setData(rates)
    }
  }

  private val statusObserver = Observer<ViewStatus> { status ->
    when (status) {
      is ViewStatus.Loading -> ui_swipe_container.isRefreshing = true
      is ViewStatus.Error -> {
        ui_swipe_container.isRefreshing = false
        ui_error_message.visible()

        // TBD: handling error to be defined by ACs, including which HTTP exceptions
        when (status.throwable) {
          is TooManyAttemptsException -> ui_error_message.text = getString(R.string.network_error)
          is InvalidResponseException -> ui_error_message.text = getString(R.string.invalid_response_error)
          is Exception -> ui_error_message.text = status.throwable.message ?: getString(R.string.network_error)
          else -> ui_error_message.text = status.message ?: getString(R.string.unknown_error)
        }
      }
      else -> {
        ui_error_message.gone()
        ui_swipe_container.isRefreshing = false
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    ui_swipe_container.setOnRefreshListener {
      mainViewModel.subscribeToRates()
    }

    ui_recycler_view.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = viewAdapter
    }

    mainViewModel.apply {
      combinedRates.observe(this@MainActivity, rateListObserver)
      viewStatus.observe(this@MainActivity, statusObserver)
      subscribeToRates()
      initCombinedRates()
    }
  }

}
