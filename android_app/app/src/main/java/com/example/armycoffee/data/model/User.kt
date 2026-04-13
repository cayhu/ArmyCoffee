package com.example.armycoffee.data.model

data class User(
    val id: Int,
    val username: String,
    val full_name: String,
    val role: String,
    val avatar: String?
)

data class LoginResponse(
    val access_token: String,
    val user: User
)
