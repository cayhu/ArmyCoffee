package com.example.armycoffee.data.api

import com.example.armycoffee.data.model.*
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: Map<String, String>): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: Map<String, String>): Map<String, Any>

    // Lấy từ /api/categories trong routes.py
    @GET("api/categories")
    suspend fun getCategories(): List<Category>

    // Lấy từ /api/products trong routes.py
    @GET("api/products")
    suspend fun getProducts(@Query("category_id") categoryId: Int? = null): List<Product>

    @POST("api/products")
    suspend fun addProduct(@Body product: Map<String, Any>): Product

    @PUT("api/products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: Map<String, Any>): Product

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Map<String, Any>

    @POST("api/checkout")
    suspend fun checkout(@Body orderData: Map<String, Any>): Map<String, Any>

    @GET("api/orders")
    suspend fun getOrders(): List<Order>

    @GET("api/orders/{id}")
    suspend fun getOrderDetail(@Path("id") orderId: Int): OrderDetailResponse

    @POST("api/orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") orderId: Int): Map<String, Any>

    // Quản lý nhân viên (Khớp với các route trong admin/routes.py nếu có api tương ứng)
    @GET("api/employees")
    suspend fun getEmployees(): List<Employee>

    @POST("api/employees")
    suspend fun addEmployee(@Body employee: Map<String, String>): Employee

    @DELETE("api/employees/{id}")
    suspend fun deleteEmployee(@Path("id") id: Int): Map<String, Any>

    // Quản lý danh mục
    @POST("api/categories")
    suspend fun addCategory(@Body category: Map<String, String>): Category

    @DELETE("api/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Int): Map<String, Any>
}

// Model cho Login
data class LoginResponse(
    val token: String?,
    val user: User?,
    val message: String?
)

// Model chi tiết đơn hàng
data class OrderDetailResponse(
    val id: Int,
    val order_code: String,
    val total: Double,
    val subtotal: Double,
    val discount: Double,
    val payment_method: String,
    val status: String,
    val note: String?,
    val created_at: String,
    val cashier: String,
    val customer: String,
    val items: List<OrderDetailItem>
)

data class OrderDetailItem(
    val name: String,
    val price: Double,
    val quantity: Int,
    val note: String?,
    val line_total: Double
)
