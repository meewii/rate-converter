package com.meewii.rateconverter.di

import com.meewii.rateconverter.App
import com.meewii.rateconverter.business.di.NetworkModule
import com.meewii.rateconverter.business.di.StorageModule
import com.meewii.rateconverter.di.viewmodels.ViewModelBinderModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    AndroidInjectionModule::class,
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ActivityModule::class,
    ClockModule::class,
    NetworkModule::class,
    StorageModule::class,
    ViewModelBinderModule::class]
)
interface AppComponent : AndroidInjector<DaggerApplication> {

  @Component.Builder
  interface Builder {

    @BindsInstance
    fun application(app: App): Builder

    fun build(): AppComponent
  }

  fun inject(app: App)

}
