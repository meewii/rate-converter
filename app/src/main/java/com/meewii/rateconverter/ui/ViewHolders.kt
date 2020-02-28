package com.meewii.rateconverter.ui

import android.content.res.ColorStateList
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.meewii.rateconverter.R
import com.meewii.rateconverter.databinding.LiRateDisplayBinding
import java.util.Locale

/**
 * View holder of the all Rates received by the server
 */
class CurrencyViewHolder(
  private val binding: LiRateDisplayBinding,
  private val onClickItem: (Currency) -> Unit,
  private val togglePinCurrency: (Currency) -> Unit
) :
  RecyclerView.ViewHolder(binding.root) {

  fun bind(item: Currency) {
    binding.apply {
      rate = item
      onClickListener = { onClickItem(item) }
      onAddToFavoriteListener = { togglePinCurrency(item) }

      val states = arrayOf(
        intArrayOf(android.R.attr.state_checked),
        intArrayOf(-android.R.attr.state_checked)
      )
      val colors = intArrayOf(
        ContextCompat.getColor(itemView.context, R.color.secondary),
        ContextCompat.getColor(itemView.context, R.color.on_surface_disabled)
      )
      uiFavoriteButton.backgroundTintList = ColorStateList(states, colors)

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