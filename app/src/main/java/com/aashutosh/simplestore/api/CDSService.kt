package com.aashutosh.simplestore.api

import com.aashutosh.simplestore.models.QuoteList
import com.aashutosh.simplestore.models.category.CategoryResponse
import com.aashutosh.simplestore.models.desimallApi.DesiDataResponse
import com.aashutosh.simplestore.models.product.ProductItem
import com.aashutosh.simplestore.models.product.Products
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface CDSService {
    @GET("quotes")
    suspend fun getQuotes(@Query("page") page: Int): Response<QuoteList>

    @GET("products?per_page=10")
    suspend fun getPageProducts(@Query("page") page: Int): Response<List<ProductItem>>

    @GET("products?per_page=10")
    suspend fun getCategoryBasedProduct(
        @Query("page") page: Int,
        @Query("category") category: Int
    ): Response<Products>


    @GET("products?per_page=5")
    suspend fun getCategoryBasedProduct5(
        @Query("page") page: Int,
        @Query("category") category: Int
    ): Response<Products>

    @GET("products")
    suspend fun getSearchProduct(
        @Query("page") page: Int,
        @Query("category") category: String,
        @Query("min_price") minPrice: String,
        @Query("max_price") maxPrice: String,
        @Query("orderby") oderBy: String,
        @Query("order") ascOrDsc: String,
        @Query("search") string: String
    ): Response<Products>


    @GET("products/categories?per_page=50")
    suspend fun getCategory(@Query("page") page: Int): Response<CategoryResponse>

    @GET("products/{id}")
    suspend fun getProductDetails(@Path("id") id: Int): ProductItem

    @GET("Item")
    suspend fun getDesiProduct(
        @Query("TokenId") id: String,
        @Query("Branchcode") branchCode: String
    ): DesiDataResponse

    @POST("orders")
    suspend fun createOrder(@Body params: RequestBody): Response<Any>

    //https://fcm.googleapis.com/fcm/send

    @POST
    suspend fun pushNotification(
        @Url url: String,
        @Header("Authorization") key: String?,
        @Header("Content-Type") accepts: String?,
        @Body jsonBody: RequestBody?
    ): okhttp3.ResponseBody

    @POST()
    fun fetchDataFromNetwork(
        @Url url: String,
        @Header("Authorization") key: String,
        @Header("Content-Type") accepts: String,
        @Body jsonBody: RequestBody
    ): Response<Any>
}