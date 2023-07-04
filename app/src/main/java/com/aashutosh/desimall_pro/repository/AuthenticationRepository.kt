package com.aashutosh.desimall_pro.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aashutosh.desimall_pro.api.APIService
import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem
import com.aashutosh.desimall_pro.models.newapi.LoginData
import com.aashutosh.desimall_pro.models.newapi.LoginResponse
import com.aashutosh.desimall_pro.models.newapi.SignUpResponse
import com.aashutosh.desimall_pro.models.newapi.UserData

import javax.inject.Inject

class AuthenticationRepository @Inject constructor(
    private val apiService: APIService
) {
    private val signUpLiveData = MutableLiveData<SignUpResponse?>()
    val signupResponse: LiveData<SignUpResponse?>
        get() = signUpLiveData



    suspend fun getSignUpData(userData: UserData): Boolean {
        return try {
            val result = apiService.registerUser(userData)
            if (result.isSuccessful) {
                signUpLiveData.postValue(result.body())
                true
            } else {
                signUpLiveData.postValue(null)
                false
            }
        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "getSignUpData: ", e)
            false
        }
    }

    suspend fun login(loginData: LoginData): LoginResponse? {
        return try {
            val response = apiService.loginUser(loginData)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "login: ", e)
            null
        }
    }
}

