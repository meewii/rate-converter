package com.meewii.rateconverter.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.meewii.rateconverter.R
import kotlinx.android.synthetic.main.li_rate.view.*

class MainListAdapter : RecyclerView.Adapter<RateViewHolder>() {

  private var data: List<Rate> = emptyList()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.li_rate, parent, false)
    return RateViewHolder(view)
  }

  override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
    val rate = data[position]
    holder.itemView.apply {
      ui_rate.contentDescription = rate.currencyName
    }
  }

  override fun getItemCount() = data.size

  fun setData(rate: List<Rate>) {
    data = rate
    notifyDataSetChanged()
  }
}

class RateViewHolder(view: View) : RecyclerView.ViewHolder(view)

data class Rate(val currencyCode: String,
                val currencyName: String,
                var value: Double,
                @DrawableRes val flag: Int)