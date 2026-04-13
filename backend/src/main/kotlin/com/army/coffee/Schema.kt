package com.army.coffee

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Categories : Table("categories") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}

object Products : Table("products") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val price = double("price")
    val categoryId = integer("category_id").references(Categories.id)
    val stockQuantity = integer("stock_quantity")
    val minStockLevel = integer("min_stock_level").default(5)
    val imageUrl = varchar("image_url", 500).nullable()
    override val primaryKey = PrimaryKey(id)
}

object InventoryLogs : Table("inventory_logs") {
    val id = integer("id").autoIncrement()
    val productId = integer("product_id").references(Products.id)
    val changeAmount = integer("change_amount")
    val type = varchar("type", 50) // "IMPORT", "SALE", "ADJUST"
    val note = varchar("note", 255).nullable()
    val timestamp = datetime("timestamp").default(LocalDateTime.now())
    override val primaryKey = PrimaryKey(id)
}

object CoffeeTables : Table("coffee_tables") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val isOccupied = bool("is_occupied").default(false)
    val qrCodeData = varchar("qr_code_data", 500).nullable()
    override val primaryKey = PrimaryKey(id)
}

object Staffs : Table("staffs") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 100).uniqueIndex()
    val password = varchar("password", 100)
    val fullName = varchar("full_name", 255)
    val role = varchar("role", 50) // "ADMIN", "STAFF"
    val isActive = bool("is_active").default(true)
    override val primaryKey = PrimaryKey(id)
}

object Orders : Table("orders") {
    val id = integer("id").autoIncrement()
    val tableId = integer("table_id").references(CoffeeTables.id)
    val tableName = varchar("table_name", 255)
    val staffId = integer("staff_id").references(Staffs.id).nullable()
    val totalAmount = double("total_amount")
    val status = varchar("status", 50).default("PAID") // "PENDING", "PAID", "CANCELLED"
    val timestamp = datetime("timestamp").default(LocalDateTime.now())
    override val primaryKey = PrimaryKey(id)
}

object OrderItems : Table("order_items") {
    val id = integer("id").autoIncrement()
    val orderId = integer("order_id").references(Orders.id)
    val productId = integer("product_id").references(Products.id)
    val productName = varchar("product_name", 255)
    val quantity = integer("quantity")
    val price = double("price")
    override val primaryKey = PrimaryKey(id)
}
