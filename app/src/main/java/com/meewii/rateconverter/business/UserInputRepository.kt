package com.meewii.rateconverter.business

import com.meewii.rateconverter.business.preferences.UserPreferences
import dagger.Reusable
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

@Reusable
class UserInputRepository @Inject constructor(private val userPreferences: UserPreferences) {

  private val userInputsSubject = BehaviorSubject.createDefault(userPreferences.getLastUserInput())

  fun setBaseRateValue(value: Double) {
    userPreferences.setLastUserInput(value)
    userInputsSubject.onNext(value)
  }

  /**
   * Returns user input Double as Flowable
   */
  fun getUserInputsStream(): Flowable<Double> {
    return userInputsSubject.toFlowable(BackpressureStrategy.LATEST)
  }
}