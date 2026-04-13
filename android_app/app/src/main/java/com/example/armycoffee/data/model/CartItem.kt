package com.example.armycoffee.data.model

data class CartItem(
    val product: Product,
    var quantity: Int = 1,
    var note: String = ""
)
