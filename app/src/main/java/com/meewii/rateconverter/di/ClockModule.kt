package com.meewii.rateconverter.di

import dagger.Module
import dagger.Provides
import org.threeten.bp.Clock
import org.threeten.bp.ZoneId

@Module
class ClockModule {

  @Provides
  fun provideClock(): Clock = Clock.system(ZoneId.of("Europe/Paris"))

}