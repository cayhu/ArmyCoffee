package com.army.coffee.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // ĐỊA CHỈ TỪ CLEVER CLOUD CỦA BẠN
    private const val BASE_URL = "https://app-18f6a634-7465-4cc4-847f-107517c51b3c.cleverapps.io/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}
