package com.aashutosh.desimall_pro.di

import com.aashutosh.desimall_pro.api.APIService
import com.aashutosh.desimall_pro.api.CDSService
import com.aashutosh.desimall_pro.utils.Constant.Companion.API_BASE_URL
import com.aashutosh.desimall_pro.utils.Constant.Companion.BASE_URL
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
    @Named("apiRetrofit")
    fun getAPIRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Singleton
    @Provides
    fun getAPIService(@Named("apiRetrofit") retrofit: Retrofit): APIService {
        return retrofit.create(APIService::class.java)
    }

    @Singleton
    @Provides
    fun getCdsService(@Named("baseRetrofit")  retrofit: Retrofit): CDSService {
        return retrofit.create(CDSService::class.java)
    }


}