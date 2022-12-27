package com.aashutosh.desimall_pro.di

import android.content.Context
import androidx.room.Room
import com.aashutosh.desimall_pro.database.DatabaseHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabaseConfig(@ApplicationContext context: Context): DatabaseHelper {
        return Room.databaseBuilder(context, DatabaseHelper::class.java, "simple_store")
            .enableMultiInstanceInvalidation()
            .build()
    }
}