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
        // Clever Cloud cung cấp biến POSTGRESQL_ADDON_URI
        val jdbcURL = System.getenv("POSTGRESQL_ADDON_URI") ?: "jdbc:postgresql://localhost:5432/army_coffee"
        val user = System.getenv("POSTGRESQL_ADDON_USER") ?: "postgres"
        val password = System.getenv("POSTGRESQL_ADDON_PASSWORD") ?: "123456"

        val database = Database.connect(createHikariDataSource(jdbcURL, user, password))

        transaction(database) {
            SchemaUtils.create(Categories, Products, Orders, OrderItems)
            // Seed data if empty
            if (Categories.selectAll().empty()) {
                Categories.insert { it[name] = "Cà phê" }
                Categories.insert { it[name] = "Trà sữa" }
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
