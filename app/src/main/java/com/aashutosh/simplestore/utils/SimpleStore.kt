package com.aashutosh.simplestore.utils

import android.app.Application
import com.aashutosh.simplestore.database.SharedPrefHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SimpleStore : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPrefHelper.init(this)
    }


}