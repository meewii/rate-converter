package com.meewii.rateconverter.di

import android.app.Application
import android.content.Context
import com.meewii.rateconverter.App
import com.meewii.rateconverter.di.viewmodels.ViewModelFactoryModule
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(includes = [ViewModelFactoryModule::class])
abstract class AppModule {

  @Binds
  @Singleton
  abstract fun application(app: App): Application

  @Binds
  @Singleton
  abstract fun context(app: App): Context
}
