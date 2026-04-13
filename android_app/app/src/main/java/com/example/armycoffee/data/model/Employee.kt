package com.example.armycoffee.data.model

data class Employee(
    val id: Int,
    val name: String,
    val role: String,
    val email: String,
    val status: String = "Active"
)
