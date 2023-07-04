package com.aashutosh.desimall_pro.models.newapi

data class SignUpResponse(
    val statusCode: Int,
    val data: User,
    val message: String
)

data class User(
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
    val _id: String,
    val __v: Int
)
