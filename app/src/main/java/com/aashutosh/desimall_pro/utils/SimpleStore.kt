package com.aashutosh.desimall_pro.utils

import android.app.Application
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SimpleStore : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPrefHelper.init(this)
    }


}