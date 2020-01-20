package com.meewii.rateconverter.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.meewii.rateconverter.databinding.LiRateDisplayBinding
import com.meewii.rateconverter.databinding.LiRateInputBinding
import kotlinx.android.synthetic.main.li_rate_input.view.ui_value
import timber.log.Timber
import java.util.Locale

class MainListAdapter(
  private val onClickItem: (Rate) -> Unit,
  private val onUserInput: (Double) -> Unit
) :
  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  class InputViewHolder(
    private val binding: LiRateInputBinding,
    private val onUserInput: (Double) -> Unit
  ) : RecyclerView.ViewHolder(binding.root) {

    private val textWatcher: TextWatcher = object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
      override fun afterTextChanged(s: Editable) {}

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        onUserInput(
          try {
            s.toString().toDouble()
          } catch (e: NumberFormatException) {
            Timber.w("NumberFormatException user input is not a number or is empty. Value reset to 1.")
            1.0
          }
        )
      }
    }

    fun bind(item: Rate) {
      binding.apply {
        rate = item
        executePendingBindings()
      }
      itemView.ui_value.removeTextChangedListener(textWatcher)
      itemView.ui_value.addTextChangedListener(textWatcher)
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
  // TBD in ACs: when should the value display 4 digits after the comma, and when should it display only 2
  if (rateValue > 9) {
    setText(String.format(Locale.getDefault(), "%.2f", rateValue))
  } else {
    setText(String.format(Locale.getDefault(), "%.4f", rateValue))
  }
}

@BindingAdapter("app:inputValue")
fun EditText.setInputValue(inputValue: String) {
  setText(inputValue)
}