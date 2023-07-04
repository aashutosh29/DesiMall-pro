package com.aashutosh.desimall_pro.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aashutosh.desimall_pro.models.newapi.LoginData
import com.aashutosh.desimall_pro.models.newapi.LoginResponse
import com.aashutosh.desimall_pro.models.newapi.SignUpResponse
import com.aashutosh.desimall_pro.models.newapi.UserData
import com.aashutosh.desimall_pro.repository.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AuthenticationViewModel @Inject constructor(private val repository: AuthenticationRepository) : ViewModel() {

    private val signUpResponseLiveData = MutableLiveData<SignUpResponse?>()
    val signUpResponse: LiveData<SignUpResponse?>
        get() = signUpResponseLiveData

    private val loginResponseLiveData = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?>
        get() = loginResponseLiveData

    fun signUp(userData: UserData) {
        viewModelScope.launch {
            val success = repository.getSignUpData(userData)
            if (success) {
                signUpResponseLiveData.postValue(repository.signupResponse.value)
            } else {
                // Handle failure case
                signUpResponseLiveData.postValue(null) // Update the LiveData value to null
            }
        }
    }


    fun login(loginData: LoginData) {
        viewModelScope.launch {
            val response = repository.login(loginData)
            loginResponseLiveData.postValue(response)
        }
    }
}

