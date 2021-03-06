package com.meewii.rateconverter.common

import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar

/**
 * Displays long Toast with given message
 */
fun Context.toast(message: String) {
  Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Displays long Toast with given string ID
 */
fun Context.toast(@StringRes messageId: Int) {
  toast(getString(messageId))
}

/**
 * Returns true if the app has been granted a particular permission.
 *
 * @param permission The name of the permission being checked.
 */
fun Context.hasPermission(permission: String): Boolean {
  return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

/**
 * Makes view visible
 */
fun View.visible() {
  this.visibility = View.VISIBLE
}

/**
 * Makes view invisible
 */
fun View.invisible() {
  this.visibility = View.INVISIBLE
}

/**
 * Makes view gone
 */
fun View.gone() {
  this.visibility = View.GONE
}

fun Snackbar.setTextColor(color: Int): Snackbar {
  val tv = view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
  tv.setTextColor(color)
  return this
}