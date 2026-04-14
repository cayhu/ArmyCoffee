package com.army.coffee.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class SuggestionRequest(val weather: String, val menu: String)

fun Route.openAiRoutes() {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
    val apiKey = "YOUR_OPENAI_API_KEY"

    post("/api/ai/suggest") {
        val request = call.receive<SuggestionRequest>()
        val prompt = "Dựa trên thời tiết là ${request.weather} và menu gồm ${request.menu}. Hãy đưa ra gợi ý món uống dưới 20 từ."

        try {
            val response: JsonObject = client.post("https://api.openai.com/v1/chat/completions") {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(buildJsonObject {
                    put("model", "gpt-3.5-turbo")
                    putJsonArray("messages") {
                        addJsonObject {
                            put("role", "user")
                            put("content", prompt)
                        }
                    }
                })
            }.body()

            val suggestion = response["choices"]?.jsonArray?.get(0)
                ?.jsonObject?.get("message")
                ?.jsonObject?.get("content")?.jsonPrimitive?.content ?: "Không có gợi ý."

            call.respond(mapOf("suggestion" to suggestion))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
        }
    }
}
