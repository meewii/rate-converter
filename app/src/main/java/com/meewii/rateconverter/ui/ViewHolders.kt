package com.meewii.rateconverter.ui

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.meewii.rateconverter.databinding.LiRateDisplayBinding
import com.meewii.rateconverter.databinding.LiRateInputBinding
import kotlinx.android.synthetic.main.li_rate_input.view.ui_value
import timber.log.Timber
import java.util.Locale

/**
 * View holder of the 1st line of the list, it can take the input of the user
 */
class InputViewHolder(private val binding: LiRateInputBinding, private val onUserInput: (Double) -> Unit) :
  RecyclerView.ViewHolder(binding.root) {

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

/**
 * View holder of the all Rates received by the server
 */
class DisplayViewHolder(private val binding: LiRateDisplayBinding, private val onClickItem: (Rate) -> Unit) :
  RecyclerView.ViewHolder(binding.root) {

  fun bind(item: Rate) {
    binding.apply {
      rate = item
      onClickListener = { onClickItem(item) }
      executePendingBindings()
    }
  }

}

@BindingAdapter("app:rateValue")
fun EditText.setRateValue(rateValue: Double) {
  setText(String.format(Locale.getDefault(), "%.2f", rateValue))
}

@BindingAdapter("app:inputValue")
fun EditText.setInputValue(inputValue: String) {
  setText(inputValue)
}