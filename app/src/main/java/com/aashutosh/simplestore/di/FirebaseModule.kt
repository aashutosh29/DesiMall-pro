package com.aashutosh.simplestore.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class FirebaseModule {
    @Singleton
    @Provides
    fun provideFireStore(): FirebaseFirestore {
        FirebaseFirestore.setLoggingEnabled(false);
        return Firebase.firestore
    }
}