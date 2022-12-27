package com.aashutosh.simplestore.sealed


sealed class StateResponse<T>(val data: T? = null, val errorMessage: String? = null) {

    class Loading<T> : StateResponse<T>()
    class Success<T>(data: T? = null) : StateResponse<T>(data = data)
    class Error<T>(errorMessage: String) : StateResponse<T>(errorMessage = errorMessage)
}

