@file:Suppress("UNCHECKED_CAST")

package com.meewii.rateconverter.di.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ViewModelFactory @Inject constructor(
  private val viewModels: Map<Class<out ViewModel>,
      @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

  @SuppressWarnings("ThrowRuntimeException")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    val creator = viewModels[modelClass] ?: viewModels.entries.firstOrNull {
      modelClass.isAssignableFrom(it.key)
    }?.value ?: throw IllegalArgumentException("Unknown model class $modelClass")
    try {
      @Suppress("UNCHECKED_CAST")
      return creator.get() as T
    } catch (e: ClassCastException) {
      throw RuntimeException("", e)
    }
  }
}