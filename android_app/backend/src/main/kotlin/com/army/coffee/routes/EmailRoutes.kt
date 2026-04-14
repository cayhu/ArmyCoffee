package com.army.coffee.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlinx.serialization.Serializable

@Serializable
data class OrderItemDto(val productName: String, val quantity: Int, val price: Double)

@Serializable
data class InvoiceRequest(
    val email: String,
    val customerName: String,
    val orderId: String,
    val items: List<OrderItemDto>,
    val totalAmount: Long
)

fun Route.emailRoutes() {
    val username = "your-email@gmail.com" // THAY THẾ BẰNG GMAIL CỦA BẠN
    val password = "your-app-password"   // THAY THẾ BẰNG APP PASSWORD

    post("/api/email/invoice") {
        val request = call.receive<InvoiceRequest>()
        
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication() = PasswordAuthentication(username, password)
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(username))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(request.email))
                setSubject("Hóa đơn đơn hàng Army Coffee - ${request.orderId}", "UTF-8")
                
                val itemRows = request.items.joinToString("") { 
                    "<tr><td style='padding:8px; border-bottom:1px solid #eee;'>${it.productName} x ${it.quantity}</td><td style='padding:8px; border-bottom:1px solid #eee; text-align:right;'>${String.format("%,.0f", it.price * it.quantity)} VNĐ</td></tr>"
                }

                val htmlBody = """
                    <div style="font-family: sans-serif; max-width: 600px; margin: auto; border: 1px solid #eee; padding: 20px; border-radius: 10px;">
                        <h2 style="color: #ea580c; text-align: center;">ARMY COFFEE</h2>
                        <p>Chào <b>${request.customerName}</b>,</p>
                        <p>Cảm ơn bạn đã tin tưởng và ủng hộ chúng tôi. Dưới đây là thông tin chi tiết hóa đơn của bạn:</p>
                        <table style="width: 100%; border-collapse: collapse; margin: 20px 0;">
                            <thead>
                                <tr style="background: #f97316; color: white;">
                                    <th style="padding: 10px; text-align: left;">Sản phẩm</th>
                                    <th style="padding: 10px; text-align: right;">Thành tiền</th>
                                </tr>
                            </thead>
                            <tbody>$itemRows</tbody>
                        </table>
                        <h3 style="text-align: right; color: #ea580c;">Tổng cộng: ${String.format("%,d", request.totalAmount)} VNĐ</h3>
                        <p style="font-size: 12px; color: #999; text-align: center; margin-top: 30px;">
                            Đây là email tự động, vui lòng không trả lời. <br>
                            Army Coffee - Địa chỉ: 123 Đường Cà Phê, Hà Nội.
                        </p>
                    </div>
                """.trimIndent()
                
                setContent(htmlBody, "text/html; charset=utf-8")
            }

            Transport.send(message)
            call.respond(mapOf("status" to "success"))
        } catch (e: Exception) {
            call.respond(mapOf("status" to "error", "message" to e.message))
        }
    }
}
