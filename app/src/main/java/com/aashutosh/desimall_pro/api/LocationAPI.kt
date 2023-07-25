package com.aashutosh.desimall_pro.api


import com.aashutosh.desimall_pro.models.NominatimResponse
import com.aashutosh.desimall_pro.models.newapi.LoginData
import com.aashutosh.desimall_pro.models.newapi.OrderData
import com.aashutosh.desimall_pro.models.newapi.OrderResponse
import com.aashutosh.desimall_pro.models.newapi.UserData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface LocationAPI {
    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("format") format: String = "json"
    ): Response<NominatimResponse>
}
