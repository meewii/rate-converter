package com.meewii.rateconverter.core

import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter

@BindingAdapter("app:textRes")
fun TextView.setTextRes(@StringRes stringId: Int) {
  if (stringId == 0) return
  setText(stringId)
}

@BindingAdapter("app:iconRes")
fun ImageView.setIconRes(@DrawableRes iconRes: Int) {
  setImageResource(iconRes)
}

@BindingAdapter("app:contentDescriptionRes")
fun ImageView.setContentDescriptionRes(@StringRes stringId: Int) {
  if (stringId == 0) return
  contentDescription = context.getString(stringId)
}
