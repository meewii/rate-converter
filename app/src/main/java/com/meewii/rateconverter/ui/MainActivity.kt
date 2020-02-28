package com.meewii.rateconverter.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.meewii.rateconverter.R
import com.meewii.rateconverter.business.InvalidResponseException
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.ui_main_container
import kotlinx.android.synthetic.main.activity_main.ui_rate_input
import kotlinx.android.synthetic.main.activity_main.ui_recycler_view
import kotlinx.android.synthetic.main.activity_main.ui_swipe_container
import kotlinx.android.synthetic.main.activity_main.ui_toolbar
import kotlinx.android.synthetic.main.inc_rate_input.view.ui_currency_code
import kotlinx.android.synthetic.main.inc_rate_input.view.ui_currency_name
import kotlinx.android.synthetic.main.inc_rate_input.view.ui_flag
import kotlinx.android.synthetic.main.inc_rate_input.view.ui_user_input_value
import org.jetbrains.annotations.TestOnly
import timber.log.Timber
import java.net.UnknownHostException
import java.util.Locale
import java.util.concurrent.TimeoutException
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var mainViewModel: MainViewModel

  private val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
  private val viewAdapter: CurrencyListAdapter = CurrencyListAdapter(::onClickItem, ::togglePinCurrency)

  @VisibleForTesting
  internal var snackBar: Snackbar? = null

  private fun onClickItem(currency: Currency) {
    mainViewModel.subscribeToRates(currency.currencyCode)
  }

  private fun togglePinCurrency(currency: Currency) {
    mainViewModel.togglePinCurrency(currency.currencyCode, currency.isPinned.not())
  }

  private val rateValueTextWatcher: TextWatcher = object : TextWatcher {
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
      mainViewModel.setBaseRateValue(
        try {
          s.toString().toDouble()
        } catch (e: NumberFormatException) {
          Timber.w("NumberFormatException user input is not a number or is empty. Value reset to 1.")
          1.0
        }
      )
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {}
  }

  private val sortedRatesObserver = Observer<Currencies> { rates ->
    rates?.let {
      if (it.sourceType == SourceType.SORTING) {
        // Uses DiffUtils callback
        viewAdapter.setData(it.list)
      } else {
        // Using DiffUtils in that case is not working, or it needs a deep copy of all currencies every time.
        // Uses notifyDataSetChanged to force refresh the list as all items are changed anyway
        viewAdapter.forceSetData(it.list)
      }
      ui_recycler_view?.let { view -> layoutManager.smoothScrollToPosition(view, null, 0) }
    }
  }

  private val baseRateObserver = Observer<Currency> { rate ->
    rate?.let {
      ui_rate_input.ui_currency_code.text = rate.currencyCode
      ui_rate_input.ui_currency_name.text = getString(rate.nameResId)
      ui_rate_input.ui_flag.setImageResource(rate.flagResId)
    }
  }

  private val lastUserInputObserver = Observer<Double> { baseValue ->
    ui_rate_input.ui_user_input_value.apply {
      removeTextChangedListener(rateValueTextWatcher)
      val formattedVal = String.format(Locale.getDefault(), "%.2f", baseValue)
      setText(formattedVal)
      hint = String.format(Locale.getDefault(), "%.2f", 1.0)
      addTextChangedListener(rateValueTextWatcher)
    }
  }

  private val statusObserver = Observer<ViewStatus> { status ->
    when (status) {
      is ViewStatus.Loading -> ui_swipe_container.isRefreshing = true
      is ViewStatus.Error -> {
        ui_swipe_container.isRefreshing = false
        val errMessage = when (status.throwable) {
          is InvalidResponseException -> getString(R.string.error_invalid_response)
          is UnknownHostException -> getString(R.string.error_no_network)
          is TimeoutException -> getString(R.string.error_no_network)
          is Exception -> status.throwable.message ?: getString(R.string.error_no_network)
          else -> status.message ?: getString(R.string.error_unknown)
        }

        snackBar = Snackbar.make(ui_main_container, errMessage, Snackbar.LENGTH_INDEFINITE)
          .setTextColor(ContextCompat.getColor(this, R.color.on_error))
          .setAction(R.string.action_dismiss) { snackBar?.dismiss() }
          .setActionTextColor(ContextCompat.getColor(this, R.color.on_error))
        snackBar?.show()
      }
      else -> {
        snackBar?.dismiss()
        ui_swipe_container.isRefreshing = false
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(ui_toolbar)

    ui_swipe_container.setOnRefreshListener {
      mainViewModel.forceRefreshRates()
    }

    ui_recycler_view.apply {
      layoutManager = this@MainActivity.layoutManager
      adapter = viewAdapter
    }

    mainViewModel.apply {
      sortedRates.observe(this@MainActivity, sortedRatesObserver)
      viewStatus.observe(this@MainActivity, statusObserver)
      baseCurrency.observe(this@MainActivity, baseRateObserver)
      lastUserInput.observe(this@MainActivity, lastUserInputObserver)
    }

  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_about -> {
        startActivity(Intent(this@MainActivity, InfoActivity::class.java))
        true
      }
      R.id.action_sort_by_name -> {
        mainViewModel.setOrder(Order.NAME)
        true
      }
      R.id.action_sort_by_ascending_rate -> {
        mainViewModel.setOrder(Order.ASCENDING_RATE)
        true
      }
      R.id.action_sort_by_descending_rate -> {
        mainViewModel.setOrder(Order.DESCENDING_RATE)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onResume() {
    super.onResume()
    mainViewModel.subscribeToRates()
  }

  @TestOnly
  internal fun setTestViewModel(testViewModel: MainViewModel) {
    mainViewModel = testViewModel
  }
}
