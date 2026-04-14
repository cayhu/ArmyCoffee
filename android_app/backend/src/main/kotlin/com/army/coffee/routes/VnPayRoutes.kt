package com.army.coffee.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun Route.vnPayRoutes() {
    val vnpTmnCode = "TCB00011"
    val vnpHashSecret = "ABCXYZ123"
    val vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"
    val vnpReturnUrl = "https://armycoffee.com/payment_return"

    post("/api/payment/vnpay-url") {
        val params = call.receive<Map<String, String>>()
        val amount = params["amount"]?.toLong() ?: 0L
        val orderInfo = params["orderInfo"] ?: "Order from Army Coffee"

        val vnpParams = mutableMapOf<String, String>()
        vnpParams["vnp_Version"] = "2.1.0"
        vnpParams["vnp_Command"] = "pay"
        vnpParams["vnp_TmnCode"] = vnpTmnCode
        vnpParams["vnp_Amount"] = (amount * 100).toString()
        vnpParams["vnp_CurrCode"] = "VND"
        vnpParams["vnp_TxnRef"] = System.currentTimeMillis().toString()
        vnpParams["vnp_OrderInfo"] = orderInfo
        vnpParams["vnp_OrderType"] = "other"
        vnpParams["vnp_Locale"] = "vn"
        vnpParams["vnp_ReturnUrl"] = vnpReturnUrl
        vnpParams["vnp_IpAddr"] = "127.0.0.1"

        val formatter = SimpleDateFormat("yyyyMMddHHmmss")
        vnpParams["vnp_CreateDate"] = formatter.format(Date())

        val fieldNames = vnpParams.keys.toMutableList()
        Collections.sort(fieldNames)

        val query = StringBuilder()
        val hashData = StringBuilder()

        for (fieldName in fieldNames) {
            val fieldValue = vnpParams[fieldName]
            if (fieldValue != null && fieldValue.isNotEmpty()) {
                // Build hash data
                hashData.append(fieldName).append("=").append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()))
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append("=")
                     .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()))
                
                if (fieldNames.indexOf(fieldName) < fieldNames.size - 1) {
                    query.append("&")
                    hashData.append("&")
                }
            }
        }

        val vnpSecureHash = hmacSHA512(vnpHashSecret, hashData.toString())
        val finalUrl = "$vnpUrl?${query}&vnp_SecureHash=$vnpSecureHash"

        call.respond(mapOf("paymentUrl" to finalUrl))
    }
}

fun hmacSHA512(key: String, data: String): String {
    val hmacSha512 = Mac.getInstance("HmacSHA512")
    val secretKey = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), "HmacSHA512")
    hmacSha512.init(secretKey)
    val hashBytes = hmacSha512.doFinal(data.toByteArray(StandardCharsets.UTF_8))
    return hashBytes.joinToString("") { "%02x".format(it) }
}
