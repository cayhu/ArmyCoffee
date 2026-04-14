package com.army.coffee.data

import com.army.coffee.data.model.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.postgresql.Driver"
        
        // Lấy thông tin từ Clever Cloud
        val host = System.getenv("POSTGRESQL_ADDON_HOST") ?: "localhost"
        val port = System.getenv("POSTGRESQL_ADDON_PORT") ?: "5432"
        val dbName = System.getenv("POSTGRESQL_ADDON_DB") ?: "army_coffee"
        val user = System.getenv("POSTGRESQL_ADDON_USER") ?: "postgres"
        val password = System.getenv("POSTGRESQL_ADDON_PASSWORD") ?: "123456"

        // Xây dựng JDBC URL chuẩn
        val jdbcURL = "jdbc:postgresql://$host:$port/$dbName"

        val database = Database.connect(createHikariDataSource(jdbcURL, user, password))

        transaction(database) {
            SchemaUtils.create(Categories, Products, Orders, OrderItems, Users, Employees)
            // Seed data if empty
            if (Categories.selectAll().empty()) {
                Categories.insert { it[name] = "Cà phê" }
                Categories.insert { it[name] = "Trà sữa" }
                Categories.insert { it[name] = "Trà trái cây" }
                Categories.insert { it[name] = "Đồ uống nóng" }
                Categories.insert { it[name] = "Thức ăn" }
            }
            if (Users.selectAll().empty()) {
                Users.insert {
                    it[username] = "admin"
                    it[password] = "1234"
                    it[fullName] = "Administrator"
                    it[role] = "ADMIN"
                }
            }
        }
    }

    private fun createHikariDataSource(url: String, user: String, pass: String) =
        HikariDataSource(HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = url
            username = user
            password = pass
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
