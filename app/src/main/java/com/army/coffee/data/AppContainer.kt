package com.army.coffee.data

import android.content.Context
import com.army.coffee.data.entities.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val coffeeRepository: CoffeeRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    private val baseUrl = "https://app-18f6a634-7465-4cc4-847f-107517c51b3c.cleverapps.io/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: CoffeeApiService by lazy {
        retrofit.create(CoffeeApiService::class.java)
    }

    // Dùng Network Repository làm repository chính cho bản 2026
    override val coffeeRepository: CoffeeRepository by lazy {
        NetworkCoffeeRepository(apiService)
    }
}
