package com.meewii.rateconverter.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.meewii.rateconverter.databinding.LiRateDisplayBinding
import com.meewii.rateconverter.databinding.LiRateInputBinding
import java.util.Locale

class MainListAdapter(private val onClickItem: (Rate) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  class InputViewHolder(private val binding: LiRateInputBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Rate) {
      binding.apply {
        rate = item
        calculatedValue = item.calculatedValue(1.0)
        executePendingBindings()
      }
    }
  }

  class DisplayViewHolder(private val binding: LiRateDisplayBinding,
                          private val onClickItem: (Rate) -> Unit) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Rate) {
      binding.apply {
        rate = item
        onClickListener = { onClickItem(item) }
        calculatedValue = item.calculatedValue(1.0)
        executePendingBindings()
      }
    }
  }

  companion object {
    const val TYPE_INPUT = 0
    const val TYPE_DISPLAY = 1
  }

  private var data: List<Rate> = emptyList()

  override fun getItemViewType(position: Int): Int {
    return if (data[position].position == 0) TYPE_INPUT else TYPE_DISPLAY
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return if (viewType == TYPE_INPUT) {
      val binding = LiRateInputBinding.inflate(inflater, parent, false)
      InputViewHolder(binding)
    } else {
      val binding = LiRateDisplayBinding.inflate(inflater, parent, false)
      DisplayViewHolder(binding, onClickItem)
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val rate = data[position]
    when (holder) {
      is InputViewHolder -> holder.bind(rate)
      is DisplayViewHolder -> holder.bind(rate)
    }
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

/**
 * Inverse binding for text string
 */
@InverseBindingAdapter(attribute = "app:rateValue")
fun EditText.getRateValue(): String? {
  return text.toString()
}

