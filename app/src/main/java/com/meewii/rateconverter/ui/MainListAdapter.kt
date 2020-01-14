package com.meewii.rateconverter.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.meewii.rateconverter.R
import kotlinx.android.synthetic.main.li_rate.view.ui_currency_code
import kotlinx.android.synthetic.main.li_rate.view.ui_currency_name
import kotlinx.android.synthetic.main.li_rate.view.ui_flag
import kotlinx.android.synthetic.main.li_rate.view.ui_value

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
      ui_currency_code.text = rate.currencyCode
      ui_currency_name.text = context.getString(rate.nameResId)
      ui_flag.contentDescription = ui_currency_name.text
      ui_flag.setImageResource(rate.flagResId)

      // TODO display comma in german, format value
      ui_value.setText(rate.calculatedValue(1.0).toString())
    }
  }

  override fun getItemCount() = data.size

  fun setData(rate: List<Rate>) {
    data = rate
    notifyDataSetChanged()
  }
}

class RateViewHolder(view: View) : RecyclerView.ViewHolder(view)