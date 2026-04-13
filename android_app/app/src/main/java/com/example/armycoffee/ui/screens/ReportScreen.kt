package com.example.armycoffee.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.armycoffee.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = Color(0xFFF1F5F9),
        topBar = {
            TopAppBar(
                title = { Text("Báo cáo kinh doanh", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Card Doanh thu chính
            item {
                RevenueMainCard()
            }

            // Thống kê nhanh
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatMiniCard("Đơn hàng", "42", Icons.Default.Receipt, Primary, Modifier.weight(1f))
                    StatMiniCard("Khách hàng", "38", Icons.Default.Groups, Secondary, Modifier.weight(1f))
                }
            }

            // Top Sản phẩm bán chạy
            item {
                Text("SẢN PHẨM BÁN CHẠY", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            }

            val topProducts = listOf(
                Triple("Cà phê sữa đá", 25, 0.8f),
                Triple("Bạc xỉu", 18, 0.6f),
                Triple("Trà đào cam sả", 12, 0.4f),
                Triple("Matcha Latte", 8, 0.25f)
            )

            items(topProducts) { (name, sold, progress) ->
                TopProductItem(name, sold, progress)
            }
        }
    }
}

@Composable
fun RevenueMainCard() {
    val gradient = Brush.linearGradient(listOf(Primary, PrimaryDark))
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .background(brush = gradient)
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Payments, null, tint = Color.White.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tổng doanh thu hôm nay", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("2,450,000đ", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = Color.White.alpha(0.2f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "📈 Tăng 15% so với hôm qua", 
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StatMiniCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).background(color.alpha(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(value, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Text(label, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun TopProductItem(name: String, sold: Int, progress: Float) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("$sold ly", fontWeight = FontWeight.Black, color = Primary, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = Primary,
                trackColor = Color(0xFFF1F5F9)
            )
        }
    }
}

// Extension function for alpha on Color
fun Color.alpha(value: Float) = this.copy(alpha = value)
