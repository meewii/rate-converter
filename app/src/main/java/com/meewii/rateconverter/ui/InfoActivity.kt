package com.meewii.rateconverter.ui

import android.os.Bundle
import android.text.Spanned
import android.text.method.LinkMovementMethod
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import com.meewii.rateconverter.R
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_info.ui_credit_list
import kotlinx.android.synthetic.main.activity_info.ui_toolbar

class InfoActivity : AppCompatActivity() {

  private val credits = mutableListOf(
    Credits(
      "Eucalyp",
      "https://www.flaticon.com/authors/eucalyp",
      "www.flaticon.com",
      "https://www.flaticon.com/",
      R.string.info_credits_app_icon
    ),
    Credits(
      "Freepik",
      "https://www.flaticon.com/authors/freepik",
      "www.flaticon.com",
      "https://www.flaticon.com/",
      R.string.info_credits_flags_icon
    )
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_info)
    setSupportActionBar(ui_toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    val builder = StringBuilder().append("<ul>")
    credits.forEach {
      builder.append("<li>")
      builder.append(getString(it.introText))
      builder.append(" ")
      builder.append("<a href=\"")
      builder.append(it.authorLink)
      builder.append("\" title=\"")
      builder.append(it.author)
      builder.append("\">")
      builder.append(it.author)
      builder.append("</a> ")
      builder.append(getString(R.string.info_credits_from))
      builder.append(" <a href=\"")
      builder.append(it.sourceLink)
      builder.append("\"> ")
      builder.append(it.source)
      builder.append("</a>\n")
      builder.append("</li>")
    }
    builder.append("</ul>")

    val all: Spanned = HtmlCompat.fromHtml(builder.toString(), FROM_HTML_MODE_LEGACY)
    ui_credit_list.movementMethod = LinkMovementMethod.getInstance()
    ui_credit_list.text = all
  }

}

data class Credits(
  val author: String, val authorLink: String, val source: String, val sourceLink: String,
  @StringRes val introText: Int
)
