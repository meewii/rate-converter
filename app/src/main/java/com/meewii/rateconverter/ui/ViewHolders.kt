package com.meewii.rateconverter.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.meewii.rateconverter.databinding.LiRateDisplayBinding
import java.util.Locale

/**
 * View holder of the all Rates received by the server
 */
class CurrencyViewHolder(private val binding: LiRateDisplayBinding, private val onClickItem: (Currency) -> Unit) :
  RecyclerView.ViewHolder(binding.root) {

  fun bind(item: Currency) {
    binding.apply {
      rate = item
      onClickListener = { onClickItem(item) }
      executePendingBindings()
    }
  }

}

@BindingAdapter("app:rateValue")
fun TextView.setRateValue(rateValue: Double) {
  text = if (rateValue >= 1) {
    String.format(Locale.getDefault(), "%.2f", rateValue)
  } else {
    String.format(Locale.getDefault(), "%.4f", rateValue)
  }
}