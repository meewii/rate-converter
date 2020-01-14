package com.meewii.rateconverter.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.meewii.rateconverter.R
import com.meewii.rateconverter.core.gone
import com.meewii.rateconverter.core.visible
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.ui_error_message
import kotlinx.android.synthetic.main.activity_main.ui_progress_bar
import kotlinx.android.synthetic.main.activity_main.ui_recycler_view
import kotlinx.android.synthetic.main.activity_main.ui_toolbar
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var mainViewModel: MainViewModel

  private val viewAdapter: MainListAdapter = MainListAdapter()

  private val rateObserver = Observer<List<Rate>> { rates ->
    rates?.let {
      viewAdapter.setData(rates)
    }
  }

  private val statusObserver = Observer<ViewStatus> { status ->
    when (status) {
      is ViewStatus.Loading -> ui_progress_bar.visible()
      is ViewStatus.Error -> {
        ui_progress_bar.gone()
        ui_error_message.visible()
        ui_error_message.text = status.message ?: getString(R.string.unknown_error)
      }
      else -> {
        ui_error_message.gone()
        ui_progress_bar.gone()
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(ui_toolbar)
    supportActionBar?.setTitle(R.string.main_activity_title)

    ui_recycler_view.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = viewAdapter
    }

    mainViewModel.apply {
      rateList.observe(this@MainActivity, rateObserver)
      viewStatus.observe(this@MainActivity, statusObserver)
    }
  }

}
