package com.aashutosh.desimall_pro.di

import com.aashutosh.desimall_pro.api.LocationAPI
import com.aashutosh.desimall_pro.api.CDSService
import com.aashutosh.desimall_pro.utils.Constant.Companion.BASE_URL
import com.aashutosh.desimall_pro.utils.Constant.Companion.LOCATION_API
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Singleton
    @Provides
    @Named("baseRetrofit")
    fun getBaseRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    @Named("locationRetrofit")
    fun getAPIRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(LOCATION_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Singleton
    @Provides
    fun getAPIService(@Named("locationRetrofit") retrofit: Retrofit): LocationAPI {
        return retrofit.create(LocationAPI::class.java)
    }

    @Singleton
    @Provides
    fun getCdsService(@Named("baseRetrofit")  retrofit: Retrofit): CDSService {
        return retrofit.create(CDSService::class.java)
    }


}