package com.aashutosh.simplestore.api

import com.aashutosh.simplestore.utils.Constant.Companion.CLIENT_ID
import com.aashutosh.simplestore.utils.Constant.Companion.CLIENT_SECRET
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request


object RetrofitHelper {

    var interceptor = Interceptor { chain: Interceptor.Chain ->
        val original: Request = chain.request()
        val request: Request = original.newBuilder()
            .header("Authorization", Credentials.basic(CLIENT_ID, CLIENT_SECRET))
            .method(original.method(), original.body())
            .build()
        chain.proceed(request)
    }

    var client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()


}