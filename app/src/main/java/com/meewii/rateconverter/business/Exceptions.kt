package com.meewii.rateconverter.business

/**
 * Error returned when the API returns a response that contains invalid data
 */
class InvalidResponseException(override val message: String) : Exception(message)

/**
 * Error returned when the API returns a response that contains an error message
 */
class ResponseErrorException(override val message: String) : Exception(message)