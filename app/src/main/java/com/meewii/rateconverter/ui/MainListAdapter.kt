package com.meewii.rateconverter.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.meewii.rateconverter.databinding.LiRateBinding
import java.util.Locale

class MainListAdapter : RecyclerView.Adapter<MainListAdapter.RateViewHolder>() {

  private val binding: LiRateBinding? = null

  private var data: List<Rate> = emptyList()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val binding = LiRateBinding.inflate(inflater, parent, false)
    return RateViewHolder(binding)
  }

  class RateViewHolder(private val binding: LiRateBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Rate) {
      binding.apply {
        rate = item
        calculatedValue = item.calculatedValue(1.0)
        executePendingBindings()
      }
    }
  }

  override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
    val rate = data[position]
    holder.bind(rate)
  }

  override fun getItemCount() = data.size

  fun setData(rate: List<Rate>) {
    data = rate
    notifyDataSetChanged()
  }
}

@BindingAdapter("app:rateValue")
fun EditText.setRateValue(rateValue: Double) {
  setText(String.format(Locale.getDefault(), "%.4f", rateValue))
}

