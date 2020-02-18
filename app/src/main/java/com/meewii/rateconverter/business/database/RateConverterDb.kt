package com.meewii.rateconverter.business.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ExchangeRateEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RateConverterDb : RoomDatabase() {

  abstract fun exchangeRateDao(): ExchangeRateDao

}