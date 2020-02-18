package com.meewii.rateconverter.business.di

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.meewii.rateconverter.business.database.ExchangeRateDao
import com.meewii.rateconverter.business.database.RateConverterDb
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StorageModule {

  @Provides
  @Singleton
  internal fun provideSharedPreferences(app: Application): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(app)

  @Provides
  @Singleton
  fun provideDatabase(application: Application): RateConverterDb =
    Room
      .databaseBuilder(application, RateConverterDb::class.java, "RateConverter.db")
      .fallbackToDestructiveMigration() // TODO: Remove for release
      .build()

  @Provides
  @Singleton
  fun provideExchangeRateDao(database: RateConverterDb): ExchangeRateDao = database.exchangeRateDao()

}