package com.meewii.rateconverter.business.network

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class LocalDateTimeConverter : JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {

  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  override fun serialize(src: LocalDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
    return JsonPrimitive(formatter.format(src))
  }

  override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime? {
    val localDate = LocalDate.from(formatter.parse(json?.asString))
    return LocalDateTime.of(localDate, LocalTime.MIDNIGHT)
  }

}

class LocalDateConverter : JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {

  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  override fun serialize(src: LocalDate?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
    return JsonPrimitive(formatter.format(src))
  }

  override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDate? {
    return LocalDate.from(formatter.parse(json?.asString))
  }

}