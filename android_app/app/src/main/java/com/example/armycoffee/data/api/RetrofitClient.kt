package com.example.armycoffee.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Sử dụng URL mới từ Clever Cloud
    private const val BASE_URL = "https://app-ccfacb40-46b5-4be9-afea-a58f4d9693f3.cleverapps.io/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}
