package com.army.coffee.data.api

import com.army.coffee.data.entities.Order
import com.army.coffee.data.entities.Product
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>

    @POST("orders")
    suspend fun createOrder(@Body order: Order): Order
}
