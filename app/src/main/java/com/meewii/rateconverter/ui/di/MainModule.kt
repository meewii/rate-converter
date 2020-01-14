package com.meewii.rateconverter.ui.di

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.meewii.rateconverter.ui.MainActivity
import com.meewii.rateconverter.ui.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [MainViewModelModule::class])
abstract class MainActivityModule {

  @Binds
  internal abstract fun bindContext(activity: MainActivity): Context

  @Binds
  internal abstract fun bindActivity(activity: MainActivity): Activity

  @Binds
  internal abstract fun bindAppCompatActivity(activity: MainActivity): AppCompatActivity

}

@Module
class MainViewModelModule {

  @Provides
  internal fun provideViewModel(activity: MainActivity, factory: ViewModelProvider.Factory)
      : MainViewModel {
    return ViewModelProvider(activity, factory).get(MainViewModel::class.java)
  }
}