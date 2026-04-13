package com.example.armycoffee.data.model

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String? = null,
    val image: String? = "☕",
    val category_id: Int = 1,
    val is_available: Boolean = true,
    // Trường bổ trợ cho UI (hiển thị tên category thay vì ID)
    val categoryName: String = ""
)
