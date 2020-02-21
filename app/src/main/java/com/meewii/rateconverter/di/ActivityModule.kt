package com.meewii.rateconverter.di

import com.meewii.rateconverter.di.scopes.ActivityScope
import com.meewii.rateconverter.ui.InfoActivity
import com.meewii.rateconverter.ui.MainActivity
import com.meewii.rateconverter.ui.di.MainActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

  @ActivityScope
  @ContributesAndroidInjector(modules = [MainActivityModule::class])
  abstract fun contributeMainActivity(): MainActivity


  @ActivityScope
  @ContributesAndroidInjector
  abstract fun contributeInfoActivity(): InfoActivity

}

