package com.army.coffee.data

import com.army.coffee.data.entities.*
import retrofit2.http.*

interface CoffeeApiService {
    @GET("categories")
    suspend fun getCategories(): List<Category>

    @POST("categories")
    suspend fun createCategory(@Body category: Category)

    @PUT("categories/{id}")
    suspend fun updateCategory(@Path("id") id: Int, @Body category: Category)

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Int)

    @GET("products")
    suspend fun getProducts(): List<Product>

    @POST("products")
    suspend fun createProduct(@Body product: Product)

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: Product)

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int)

    @GET("tables")
    suspend fun getTables(): List<CoffeeTable>

    @POST("tables")
    suspend fun createTable(@Body table: CoffeeTable)

    @PATCH("tables/{id}/status")
    suspend fun updateTableStatus(@Path("id") id: Int, @Body status: Map<String, Boolean>)

    @DELETE("tables/{id}")
    suspend fun deleteTable(@Path("id") id: Int)

    @GET("orders")
    suspend fun getOrders(): List<Order>

    @POST("orders")
    suspend fun createOrder(@Body order: OrderRequest)
}

data class OrderRequest(
    val id: Int? = null,
    val tableId: Int,
    val tableName: String,
    val totalAmount: Double,
    val items: List<OrderItemRequest> = emptyList()
)

data class OrderItemRequest(
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val price: Double
)
