package com.aashutosh.desimall_pro.api


import com.aashutosh.desimall_pro.models.newapi.LoginData
import com.aashutosh.desimall_pro.models.newapi.LoginResponse
import com.aashutosh.desimall_pro.models.newapi.OrderData
import com.aashutosh.desimall_pro.models.newapi.OrderResponse
import com.aashutosh.desimall_pro.models.newapi.SignUpResponse
import com.aashutosh.desimall_pro.models.newapi.UserData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface APIService {
    @POST("user_register/user_register")
    suspend fun registerUser(@Body userData: UserData): Response<SignUpResponse>

    @POST("user_register/login")
    suspend fun loginUser(@Body loginData: LoginData): Response<LoginResponse>

    @POST("/order/order")
    suspend fun placeOrder(@Body orderData: OrderData): Response<Any>

    @GET("/order/get_order/{phoneNumber}")
    suspend fun getOrders(@Path("phoneNumber") phoneNumber: String): Response<OrderResponse>

}