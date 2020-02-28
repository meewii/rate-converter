package com.meewii.rateconverter.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.meewii.rateconverter.databinding.LiRateDisplayBinding

/**
 * MainActivity's currency adapter
 */
class CurrencyListAdapter(private val onClickItem: (Currency) -> Unit,
                          private val togglePinCurrency: (Currency) -> Unit) :
  RecyclerView.Adapter<CurrencyViewHolder>() {

  @VisibleForTesting
  internal val data: ArrayList<Currency> = arrayListOf()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val binding = LiRateDisplayBinding.inflate(inflater, parent, false)
    return CurrencyViewHolder(binding, onClickItem, togglePinCurrency)
  }

  override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
    holder.bind(data[position])
  }

  override fun getItemCount() = data.size

  fun setData(currency: List<Currency>) {
    val diffCallback = CurrencyDiffCallback(data, currency)
    val diffResult = DiffUtil.calculateDiff(diffCallback)

    data.clear()
    data.addAll(currency)
    diffResult.dispatchUpdatesTo(this)
  }


  fun forceSetData(currency: List<Currency>) {
    data.clear()
    data.addAll(currency)
    notifyDataSetChanged()
  }

}

class CurrencyDiffCallback(private val oldList: List<Currency>, private val newList: List<Currency>) :
  DiffUtil.Callback() {

  override fun getOldListSize() = oldList.size

  override fun getNewListSize() = newList.size

  override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    return oldList[oldItemPosition].currencyCode == newList[newItemPosition].currencyCode
  }

  override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    return oldList[oldItemPosition].calculatedValue == newList[newItemPosition].calculatedValue
  }

}