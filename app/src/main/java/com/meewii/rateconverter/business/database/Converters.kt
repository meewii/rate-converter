package com.meewii.rateconverter.business.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class Converters {

  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  @TypeConverter
  fun rateMapToJson(rates: Map<String, Double>?): String? {
    return Gson().toJson(rates)
  }

  @TypeConverter
  fun rateMapFromJson(json: String): Map<String, Double>? {
    val mapType: Type = object : TypeToken<Map<String, Double>?>() {}.type
    return Gson().fromJson(json, mapType)
  }

  @TypeConverter
  fun localDateTimeToString(localDateTime: LocalDateTime?): String? {
    return localDateTime?.format(formatter) ?: "null"
  }

  @TypeConverter
  fun localDateTimeFromString(date: String?): LocalDateTime? {
    if (date == null || date == "null") return null
    return LocalDateTime.parse(date, formatter)
  }

}