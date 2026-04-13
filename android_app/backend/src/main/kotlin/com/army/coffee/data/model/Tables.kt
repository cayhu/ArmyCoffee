package com.army.coffee.data.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Categories : Table("categories") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    override val primaryKey = PrimaryKey(id)
}

object Products : Table("products") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val price = double("price")
    val description = varchar("description", 500).nullable()
    val image = varchar("image", 500).default("☕")
    val categoryId = integer("category_id") references Categories.id
    val isAvailable = bool("is_available").default(true)
    override val primaryKey = PrimaryKey(id)
}

object Orders : Table("orders") {
    val id = integer("id").autoIncrement()
    val orderCode = varchar("order_code", 50)
    val total = double("total")
    val paymentMethod = varchar("payment_method", 50)
    val status = varchar("status", 50)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val itemCount = integer("item_count")
    val cashier = varchar("cashier", 100)
    val customer = varchar("customer", 100).default("Khách vãng lai")
    override val primaryKey = PrimaryKey(id)
}

object OrderItems : Table("order_items") {
    val id = integer("id").autoIncrement()
    val orderId = integer("order_id") references Orders.id
    val productId = integer("product_id") references Products.id
    val quantity = integer("quantity")
    val price = double("price")
    override val primaryKey = PrimaryKey(id)
}

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 100)
    val fullName = varchar("full_name", 100)
    val role = varchar("role", 20) // "ADMIN", "CASHIER"
    val avatar = varchar("avatar", 500).nullable()
    override val primaryKey = PrimaryKey(id)
}

object Employees : Table("employees") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val role = varchar("role", 50)
    val email = varchar("email", 100)
    val status = varchar("status", 20).default("Đang làm việc")
    override val primaryKey = PrimaryKey(id)
}
