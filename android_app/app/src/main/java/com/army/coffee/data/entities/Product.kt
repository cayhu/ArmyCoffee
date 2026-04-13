package com.army.coffee.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val categoryId: Int,
    val stockQuantity: Int = 100, // Thêm số lượng tồn kho
    val imageUrl: String? = null
)
