package com.meewii.rateconverter.common

import timber.log.Timber

/**
 * Displays Line number and Method name in Log messages
 */
class LineNumberDebugTree : Timber.DebugTree() {
  override fun createStackElementTag(element: StackTraceElement): String? {
    return "(${element.fileName}:${element.lineNumber})#${element.methodName}"
  }
}