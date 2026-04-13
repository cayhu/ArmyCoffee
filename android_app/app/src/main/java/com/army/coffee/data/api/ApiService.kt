package com.army.coffee.data.api

import com.army.coffee.data.model.Order
import com.army.coffee.data.model.Product
import com.army.coffee.data.model.Category
import com.army.coffee.data.model.Employee
import com.army.coffee.data.model.User
import retrofit2.http.*

interface ApiService {
    // Products
    @GET("products")
    suspend fun getProducts(): List<Product>

    @POST("products")
    suspend fun addProduct(@Body product: Map<String, Any>): Product

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: Map<String, Any>)

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int)

    // Orders
    @POST("orders")
    suspend fun createOrder(@Body order: Order): Order

    // Categories
    @GET("categories")
    suspend fun getCategories(): List<CategoryResponse>

    @POST("categories")
    suspend fun addCategory(@Body category: Map<String, String>): CategoryResponse

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Int)

    // Employees
    @GET("employees")
    suspend fun getEmployees(): List<Employee>

    @POST("employees")
    suspend fun addEmployee(@Body employee: Map<String, String>): Employee

    @DELETE("employees/{id}")
    suspend fun deleteEmployee(@Path("id") id: Int)

    // Auth
    @POST("login")
    suspend fun login(@Body credentials: Map<String, String>): LoginResponse
}

data class LoginResponse(val user: User?, val token: String? = null)

data class CategoryResponse(val id: Int, val name: String, val icon: String)
