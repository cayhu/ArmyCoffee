package com.example.armycoffee.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.automirrored.rounded.FactCheck
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.armycoffee.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreen(
    userRole: String,
    onNavigateToMenu: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToEmployees: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToPriceSettings: () -> Unit
) {
    val managementItems = mutableListOf<ManagementItem>().apply {
        add(ManagementItem("Hàng hóa", Icons.Rounded.Inventory, Color(0xFF10B981), onNavigateToProducts))
        add(ManagementItem("Danh mục", Icons.Rounded.Category, Color(0xFF3B82F6), onNavigateToCategories))
        add(ManagementItem("Hóa đơn", Icons.Rounded.Receipt, Color(0xFFF59E0B), onNavigateToOrders))
        add(ManagementItem("Khách hàng", Icons.Rounded.People, Color(0xFF06B6D4), {}))
        add(ManagementItem("Nhà cung cấp", Icons.Rounded.LocalShipping, Color(0xFF6366F1), {}))
        add(ManagementItem("Kiểm kho", Icons.AutoMirrored.Rounded.FactCheck, Color(0xFFEC4899), onNavigateToInventory))
        
        if (userRole == "ADMIN") {
            add(ManagementItem("Báo cáo", Icons.Rounded.Assessment, Color(0xFF8B5CF6), onNavigateToReports))
            add(ManagementItem("Nhân viên", Icons.Rounded.Badge, Color(0xFFF43F5E), onNavigateToEmployees))
            add(ManagementItem("Thiết lập giá", Icons.Rounded.Sell, Color(0xFF14B8A6), onNavigateToPriceSettings))
        }
    }

    Scaffold(
        containerColor = Color(0xFFF1F5F9),
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.shadow(16.dp)
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToMenu,
                    icon = { Icon(Icons.Outlined.Storefront, null) },
                    label = { Text("Bán hàng") },
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color(0xFF94A3B8), unselectedTextColor = Color(0xFF94A3B8))
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToOrders,
                    icon = { Icon(Icons.AutoMirrored.Outlined.ReceiptLong, null) },
                    label = { Text("Hóa đơn") },
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color(0xFF94A3B8), unselectedTextColor = Color(0xFF94A3B8))
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("Quản lý", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Primary, selectedTextColor = Primary, indicatorColor = Primary.copy(alpha = 0.1f))
                )
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            // 1. Profile Header
            item(span = { GridItemSpan(3) }) {
                KiotVietProfileHeader(userRole)
            }

            // 2. Sales Summary Card
            item(span = { GridItemSpan(3) }) {
                KiotVietSalesSummary()
            }

            // 3. Management Grid Header
            item(span = { GridItemSpan(3) }) {
                Box(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text("QUẢN LÝ CỬA HÀNG", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
            }

            // 4. Square Grid Items
            items(managementItems) { item ->
                KiotVietSquareItem(item)
            }

            // 5. System Sections
            item(span = { GridItemSpan(3) }) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    KiotVietSectionHeader("TIỆN ÍCH")
                    KiotVietListItem("Thiết lập cửa hàng", Icons.Rounded.Settings, onNavigateToSettings)
                    KiotVietListItem("Quản lý chi nhánh", Icons.Rounded.Store, {})
                    
                    KiotVietSectionHeader("HỖ TRỢ")
                    KiotVietListItem("Tổng đài hỗ trợ (1900 xxxx)", Icons.Rounded.HeadsetMic, {})
                    KiotVietListItem("Hướng dẫn sử dụng", Icons.Rounded.MenuBook, {})
                    KiotVietListItem("Đăng xuất", Icons.AutoMirrored.Outlined.Logout, onLogout, isLast = true, tint = Color.Red)
                }
            }

            // 6. Version Info
            item(span = { GridItemSpan(3) }) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Phiên bản 1.0.0", color = Color.LightGray, fontSize = 12.sp)
                    Text("Army Coffee - Quản lý chuyên nghiệp", color = Color.LightGray, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun KiotVietProfileHeader(userRole: String) {
    Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape).background(Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text("AC", color = Primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Army Coffee", fontWeight = FontWeight.Black, fontSize = 17.sp, color = Color(0xFF1E293B))
                Text(if(userRole == "ADMIN") "Chủ cửa hàng" else "Nhân viên", color = Color.Gray, fontSize = 13.sp)
            }
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun KiotVietSalesSummary() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Kết quả bán hàng hôm nay", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
                Icon(Icons.Rounded.Refresh, null, tint = Primary, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("0", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF10B981))
                    Text("Hóa đơn", fontSize = 11.sp, color = Color.Gray)
                }
                Box(modifier = Modifier.width(1.dp).height(35.dp).background(Color(0xFFF1F5F9)))
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("0đ", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF3B82F6))
                    Text("Doanh thu", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun KiotVietSquareItem(item: ManagementItem) {
    Surface(
        onClick = item.onClick,
        modifier = Modifier.aspectRatio(1f).background(Color.White),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = item.icon, contentDescription = null, tint = item.iconColor, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun KiotVietSectionHeader(title: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
    }
}

@Composable
fun KiotVietListItem(title: String, icon: ImageVector, onClick: () -> Unit, isLast: Boolean = false, tint: Color = Color(0xFF475569)) {
    Surface(onClick = onClick, color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, modifier = Modifier.weight(1f), fontSize = 14.sp, color = if(tint == Color.Red) Color.Red else Color(0xFF1E293B))
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
            }
            if (!isLast) {
                HorizontalDivider(modifier = Modifier.padding(start = 52.dp), color = Color(0xFFF1F5F9), thickness = 1.dp)
            }
        }
    }
}

data class ManagementItem(
    val title: String,
    val icon: ImageVector,
    val iconColor: Color,
    val onClick: () -> Unit
)
