package com.example.armycoffee.data.model

data class Category(
    val id: Int,
    val name: String,
    val icon: String = "📁",
    val is_active: Boolean = true
)
