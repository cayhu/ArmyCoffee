package com.army.coffee

import com.army.coffee.data.DatabaseFactory
import com.army.coffee.data.model.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*

fun main() {
    DatabaseFactory.init()
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    routing {
        get("/") {
            call.respondText("Army Coffee Backend is Running!")
        }

        route("/api") {
            get("/products") {
                val productsList = DatabaseFactory.dbQuery {
                    (Products innerJoin Categories)
                        .selectAll()
                        .map {
                            ProductDTO(
                                id = it[Products.id],
                                name = it[Products.name],
                                price = it[Products.price],
                                description = it[Products.description],
                                image = it[Products.image],
                                category_id = it[Products.categoryId],
                                is_available = it[Products.isAvailable],
                                categoryName = it[Categories.name]
                            )
                        }
                }
                call.respond(productsList)
            }

            post("/orders") {
                val orderDto = call.receive<OrderDTO>()
                val savedOrder = DatabaseFactory.dbQuery {
                    val inserted = Orders.insert {
                        it[orderCode] = orderDto.order_code
                        it[total] = orderDto.total
                        it[paymentMethod] = orderDto.payment_method
                        it[status] = orderDto.status
                        it[itemCount] = orderDto.item_count
                        it[cashier] = orderDto.cashier
                        it[customer] = orderDto.customer
                    }
                    orderDto.copy(id = inserted[Orders.id])
                }
                call.respond(savedOrder)
            }
        }
    }
}
