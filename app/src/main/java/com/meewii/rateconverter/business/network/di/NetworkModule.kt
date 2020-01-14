package com.meewii.rateconverter.business.network.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.meewii.rateconverter.business.network.RateService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module
class NetworkModule {

  companion object {
    const val BASE_URL = "https://revolut.duckdns.org"
  }

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .build()
  }

  @Provides
  fun provideRateService(retrofit: Retrofit): RateService {
    return retrofit.create(RateService::class.java)
  }

  @Provides
  fun provideGson(): Gson {
    return GsonBuilder().create()
  }

  @Provides
  fun provideInterceptor(): HttpLoggingInterceptor {
    val interceptor = HttpLoggingInterceptor { message -> Timber.i(message) }
    interceptor.level = HttpLoggingInterceptor.Level.BASIC
    return interceptor
  }

  @Provides
  fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(interceptor)
      .build()
  }

}