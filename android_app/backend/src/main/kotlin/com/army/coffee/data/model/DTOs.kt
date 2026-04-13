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
data class CategoryDTO(
    val id: Int,
    val name: String,
    val icon: String
)

@Serializable
data class EmployeeDTO(
    val id: Int,
    val name: String,
    val role: String,
    val email: String,
    val status: String
)

@Serializable
data class UserDTO(
    val id: Int,
    val username: String,
    val full_name: String,
    val role: String,
    val avatar: String? = null
)

@Serializable
data class LoginResponseDTO(
    val user: UserDTO,
    val access_token: String = "fake-token-123"
)
