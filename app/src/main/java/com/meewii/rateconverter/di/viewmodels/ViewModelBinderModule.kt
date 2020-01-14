package com.meewii.rateconverter.di.viewmodels

import androidx.lifecycle.ViewModel
import com.meewii.rateconverter.ui.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelBinderModule {

  @Binds
  @IntoMap
  @ViewModelKey(MainViewModel::class)
  internal abstract fun mainViewModel(viewModel: MainViewModel): ViewModel

}