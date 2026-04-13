package com.example.armycoffee.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.armycoffee.data.model.Order
import com.example.armycoffee.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onNavigateToMenu: () -> Unit,
    onNavigateToManagement: () -> Unit,
    onLogout: () -> Unit
) {
    // Dữ liệu mẫu hóa đơn (Đã được KiotViet hóa)
    val mockOrders = listOf(
        Order(1, "AC-0001", 54000.0, "cash", "completed", "14:20", 2, "Tân"),
        Order(2, "AC-0002", 125000.0, "qr", "completed", "13:45", 4, "Tân"),
        Order(3, "AC-0003", 29000.0, "ewallet", "completed", "12:10", 1, "Tân"),
        Order(4, "AC-0004", 45000.0, "cash", "cancelled", "10:30", 2, "Tân")
    )

    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFF1F5F9),
        topBar = {
            Column(modifier = Modifier.background(Primary).statusBarsPadding()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateToMenu) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                        }
                        Text("Lịch sử hóa đơn", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Outlined.Logout, null, tint = Color.White)
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToMenu,
                    icon = { Icon(Icons.Outlined.Storefront, null) },
                    label = { Text("Bán hàng") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.ReceiptLong, null) },
                    label = { Text("Hóa đơn") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Primary, selectedTextColor = Primary)
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToManagement,
                    icon = { Icon(Icons.Outlined.Settings, null) },
                    label = { Text("Quản lý") }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Thanh tổng kết doanh thu nhanh
            Surface(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("TỔNG ĐƠN", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("${mockOrders.size}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Primary)
                    }
                    val totalRevenue = mockOrders.filter { it.status == "completed" }.sumOf { it.total }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("TỔNG DOANH THU", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("${String.format("%,.0f", totalRevenue)}đ", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Secondary)
                    }
                }
            }

            // Danh sách hóa đơn
            Text(
                "DANH SÁCH GIAO DỊCH", 
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray
            )
            
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(mockOrders) { order ->
                    OrderListItem(order) {
                        selectedOrder = order
                        showDetailDialog = true
                    }
                }
            }
        }
    }

    if (showDetailDialog && selectedOrder != null) {
        OrderReceiptDialog(order = selectedOrder!!, onDismiss = { showDetailDialog = false })
    }
}

@Composable
fun OrderListItem(order: Order, onClick: () -> Unit) {
    val isCancelled = order.status == "cancelled"
    
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).background(if (isCancelled) Color(0xFFFEE2E2) else Color(0xFFDCFCE7), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isCancelled) Icons.Default.Close else Icons.Default.Check, 
                    null, 
                    tint = if (isCancelled) Danger else Color(0xFF166534)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(order.order_code, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("${order.created_at} • ${order.payment_method.uppercase()}", fontSize = 12.sp, color = Color.Gray)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${String.format("%,.0f", order.total)}đ", 
                    fontWeight = FontWeight.Black, 
                    fontSize = 16.sp,
                    color = if (isCancelled) Color.Gray else Primary
                )
                if (isCancelled) {
                    Text("Đã hủy", color = Danger, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun OrderReceiptDialog(order: Order, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                Text("Đóng hóa đơn")
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Text("ARMY COFFEE 🎖️", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Text("Hóa đơn thanh toán", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 12.sp, color = Color.Gray)
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                Spacer(modifier = Modifier.height(8.dp))
                
                DetailLine("Mã đơn hàng:", order.order_code)
                DetailLine("Thời gian:", order.created_at)
                DetailLine("Nhân viên:", order.cashier)
                DetailLine("Thanh toán:", order.payment_method.uppercase())
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("CHI TIẾT MÓN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                
                // Giả lập danh sách món
                listOf("Cà phê sữa đá" to 29000, "Bạc xỉu" to 35000).forEach { (name, price) ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(name, fontSize = 14.sp)
                        Text("${String.format("%,.0f", price.toDouble())}đ", fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 2.dp, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TỔNG CỘNG", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Text("${String.format("%,.0f", order.total)}đ", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Primary)
                }
                
                if (order.status == "cancelled") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("ĐƠN HÀNG ĐÃ HỦY", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Danger, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                Text("Cảm ơn Quý khách! Hẹn gặp lại!", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 12.sp, color = Color.Gray)
            }
        }
    )
}

@Composable
fun DetailLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
