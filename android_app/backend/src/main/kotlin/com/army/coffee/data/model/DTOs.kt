package com.army.coffee.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductDTO(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String? = null,
    val image: String? = "☕",
    val category_id: Int,
    val is_available: Boolean,
    val categoryName: String = ""
)

@Serializable
data class OrderDTO(
    val id: Int? = null,
    val order_code: String,
    val total: Double,
    val payment_method: String,
    val status: String,
    val created_at: String? = null,
    val item_count: Int,
    val cashier: String,
    val customer: String = "Khách vãng lai"
)
