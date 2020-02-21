package com.meewii.rateconverter

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.meewii.rateconverter.common.LineNumberDebugTree
import com.meewii.rateconverter.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject

class App : Application(), HasAndroidInjector {

  @Inject
  lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

  override fun androidInjector(): AndroidInjector<Any> {
    return dispatchingAndroidInjector
  }

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(LineNumberDebugTree())
    }
    AndroidThreeTen.init(this)
    DaggerAppComponent.builder().application(this)
      .build()
      .inject(this)
  }

}