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
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    try {
        DatabaseFactory.init()
    } catch (e: Exception) {
        log.error("Database connection failed: ${e.message}")
    }

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
            post("/login") {
                val credentials = call.receive<Map<String, String>>()
                val username = credentials["username"]
                val password = credentials["password"]

                val user = DatabaseFactory.dbQuery {
                    Users.select { (Users.username eq username!!) and (Users.password eq password!!) }
                        .map {
                            UserDTO(
                                id = it[Users.id],
                                username = it[Users.username],
                                full_name = it[Users.fullName],
                                role = it[Users.role],
                                avatar = it[Users.avatar]
                            )
                        }.singleOrNull()
                }

                if (user != null) {
                    call.respond(LoginResponseDTO(user = user))
                } else {
                    call.respond(io.ktor.http.HttpStatusCode.Unauthorized, "Sai tài khoản hoặc mật khẩu")
                }
            }

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

            get("/categories") {
                val categoriesList = DatabaseFactory.dbQuery {
                    Categories.selectAll().map {
                        CategoryDTO(
                            id = it[Categories.id],
                            name = it[Categories.name],
                            icon = "☕" // Default icon
                        )
                    }
                }
                call.respond(categoriesList)
            }

            get("/employees") {
                val employeesList = DatabaseFactory.dbQuery {
                    Employees.selectAll().map {
                        EmployeeDTO(
                            id = it[Employees.id],
                            name = it[Employees.name],
                            role = it[Employees.role],
                            email = it[Employees.email],
                            status = it[Employees.status]
                        )
                    }
                }
                call.respond(employeesList)
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
