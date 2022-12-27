package com.aashutosh.simplestore.di

import com.aashutosh.simplestore.api.CDSService
import com.aashutosh.simplestore.api.RetrofitHelper
import com.aashutosh.simplestore.utils.Constant.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Singleton
    @Provides
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun getCdsService(retrofit: Retrofit): CDSService {
        return retrofit.create(CDSService::class.java)
    }


}