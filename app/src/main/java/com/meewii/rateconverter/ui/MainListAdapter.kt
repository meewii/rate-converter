package com.meewii.rateconverter.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.meewii.rateconverter.databinding.LiRateDisplayBinding
import com.meewii.rateconverter.databinding.LiRateInputBinding

/**
 * MainActivity's rates adapter. Uses 2 types of holder depending on the Rate's position.
 */
class MainListAdapter(private val onClickItem: (Rate) -> Unit, private val onUserInput: (Double) -> Unit)
  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
      InputViewHolder(binding, onUserInput)
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