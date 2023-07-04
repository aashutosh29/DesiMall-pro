package com.aashutosh.desimall_pro.models.newapi

data class LoginResponse(
    val statusCode: Int,
    val data: LoginDetails?,
    val message: String,
    val token: String?
)

data class LoginDetails(
    val _id: String,
    val code: String,
    val firstName: String,
    val lastName: String,
    val userAddress: String,
    val city: String,
    val pin: String,
    val state: String,
    val country: String,
    val mobile: String,
    val dob: String,
    val password: String,
    val cPassword: String,
    val email: String,
    val __v: Int
)
