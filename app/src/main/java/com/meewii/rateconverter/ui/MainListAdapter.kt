package com.meewii.rateconverter.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.meewii.rateconverter.databinding.LiRateDisplayBinding
import com.meewii.rateconverter.databinding.LiRateInputBinding
import kotlinx.android.synthetic.main.li_rate_input.view.ui_value
import timber.log.Timber
import java.util.Locale

class MainListAdapter(private val onClickItem: (Rate) -> Unit,
                      private val onUserInput: (Double) -> Unit) :
  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  class InputViewHolder(private val binding: LiRateInputBinding,
                        private val onUserInput: (Double) -> Unit) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Rate) {
      binding.apply {
        rate = item
        executePendingBindings()
      }
      itemView.ui_value.addTextChangedListener {
        Timber.v("AAA $it")
        onUserInput(try {
          it.toString().toDouble()
        } catch (e: NumberFormatException) {
          Timber.w("NumberFormatException user input is not a number or is empty. Value reset to 1.")
          1.0
        })
      }
    }
  }

  class DisplayViewHolder(
    private val binding: LiRateDisplayBinding,
    private val onClickItem: (Rate) -> Unit
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Rate) {
      binding.apply {
        rate = item
        onClickListener = { onClickItem(item) }
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

@BindingAdapter("app:rateValue")
fun EditText.setRateValue(rateValue: Double) {
  setText(String.format(Locale.getDefault(), "%.4f", rateValue))
}

@BindingAdapter("app:inputValue")
fun EditText.setInputValue(inputValue: String) {
  setText(inputValue)
}