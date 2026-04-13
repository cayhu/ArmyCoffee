package com.example.armycoffee.data.model

data class Order(
    val id: Int,
    val order_code: String,
    val total: Double,
    val payment_method: String,
    val status: String,
    val created_at: String,
    val item_count: Int,
    val cashier: String,
    val customer: String = "Khách vãng lai"
)
