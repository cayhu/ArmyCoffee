package com.example.armycoffee.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.armycoffee.data.model.Product
import com.example.armycoffee.ui.theme.*
import com.example.armycoffee.ui.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    userRole: String,
    onNavigateToOrders: () -> Unit,
    onNavigateToManagement: () -> Unit,
    onLogout: () -> Unit,
    viewModel: MenuViewModel = viewModel()
) {
    var showCartSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showCheckoutSuccess by remember { mutableStateOf(false) }

    val headerGradient = Brush.verticalGradient(
        colors = listOf(Primary, PrimaryDark)
    )

    Scaffold(
        containerColor = Color(0xFFF1F5F9), // Light background
        topBar = {
            Column(
                modifier = Modifier
                    .background(headerGradient)
                    .statusBarsPadding()
            ) {
                // Header với thông tin Quán & Nhân viên
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Army Coffee 🎖️", 
                            color = Color.White, 
                            fontSize = 20.sp, 
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            "Chào ngày mới, Tân!", 
                            color = Color.White.copy(alpha = 0.8f), 
                            fontSize = 12.sp
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Nút Đăng xuất
                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Outlined.Logout, null, tint = Color.White)
                        }
                    }
                }

                // Thanh tìm kiếm phong cách KiotViet
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    TextField(
                        value = viewModel.searchText,
                        onValueChange = { viewModel.searchText = it },
                        placeholder = { Text("Tìm tên món hoặc mã...", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Rounded.Search, null) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                // Danh mục sản phẩm (Tabs Pills)
                LazyRow(
                    modifier = Modifier.padding(vertical = 12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        CategoryTab(
                            name = "Tất cả", 
                            isSelected = viewModel.selectedCategoryId == 0,
                            onClick = { viewModel.selectedCategoryId = 0 }
                        )
                    }
                    items(viewModel.categories) { cat ->
                        CategoryTab(
                            name = cat.name, 
                            icon = cat.icon,
                            isSelected = viewModel.selectedCategoryId == cat.id,
                            onClick = { viewModel.selectedCategoryId = cat.id }
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column {
                // Thanh tóm tắt giỏ hàng (Hiện khi có món)
                AnimatedVisibility(
                    visible = viewModel.cartCount > 0,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCartSheet = true },
                        color = Secondary,
                        contentColor = Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.ShoppingCart, null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("${viewModel.cartCount} món đã chọn", fontWeight = FontWeight.Bold)
                            }
                            Text(
                                "${String.format("%,.0f", viewModel.cartTotal)}đ >",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                    }
                }

                // Navigation Bar
                NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { },
                        icon = { Icon(Icons.Default.Storefront, null) },
                        label = { Text("Bán hàng") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Primary, selectedTextColor = Primary)
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToOrders,
                        icon = { Icon(Icons.Outlined.ReceiptLong, null) },
                        label = { Text("Hóa đơn") }
                    )
                    if (userRole == "ADMIN") {
                        NavigationBarItem(
                            selected = false,
                            onClick = onNavigateToManagement,
                            icon = { Icon(Icons.Outlined.Settings, null) },
                            label = { Text("Quản lý") }
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val filteredProducts = viewModel.products.filter { 
                (viewModel.selectedCategoryId == 0 || it.category_id == viewModel.selectedCategoryId) &&
                (it.name.contains(viewModel.searchText, ignoreCase = true))
            }
            items(filteredProducts) { product ->
                POSProductCard(product) { viewModel.addToCart(product) }
            }
        }
    }

    // Giỏ hàng (Modal Bottom Sheet)
    if (showCartSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCartSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
                Text(
                    "ĐƠN HÀNG HIỆN TẠI", 
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Column(modifier = Modifier.heightIn(max = 300.dp).verticalScroll(rememberScrollState())) {
                    viewModel.cart.forEach { item ->
                        CartItemRow(item, 
                            onIncrement = { viewModel.updateQuantity(item.product.id, 1) }, 
                            onDecrement = { viewModel.updateQuantity(item.product.id, -1) }
                        )
                    }
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                // Phương thức thanh toán
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentButton("Tiền mặt", viewModel.paymentMethod == "cash") { viewModel.paymentMethod = "cash" }
                    PaymentButton("Chuyển khoản", viewModel.paymentMethod == "qr") { viewModel.paymentMethod = "qr" }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nút Thanh toán lớn
                Button(
                    onClick = { 
                        viewModel.checkout { 
                            showCartSheet = false
                            showCheckoutSuccess = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "THANH TOÁN ${String.format("%,.0f", viewModel.cartTotal)}đ", 
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

    if (showCheckoutSuccess) {
        AlertDialog(
            onDismissRequest = { showCheckoutSuccess = false },
            confirmButton = {
                TextButton(onClick = { showCheckoutSuccess = false }) { Text("OK", fontWeight = FontWeight.Bold) }
            },
            title = { Text("Thanh toán xong! ✅") },
            text = { Text("Hóa đơn đã được lưu vào hệ thống Army Coffee.") }
        )
    }
}

@Composable
fun CartItemRow(
    item: com.example.armycoffee.data.model.CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFF1F5F9)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(item.product.image ?: "☕", fontSize = 26.sp)
            }
        }

        Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            Text(item.product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
            Text("${String.format("%,.0f", item.product.price)}đ", color = Primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp)).padding(4.dp)
        ) {
            IconButton(onClick = onDecrement, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp), tint = Color(0xFF64748B))
            }
            Text(item.quantity.toString(), modifier = Modifier.width(24.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Black, fontSize = 14.sp)
            IconButton(onClick = onIncrement, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = Primary)
            }
        }
    }
}

@Composable
fun CategoryTab(name: String, icon: String = "", isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
        contentColor = if (isSelected) Primary else Color.White
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (icon.isNotBlank()) Text(icon, modifier = Modifier.padding(end = 4.dp))
            Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun PaymentButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() }.height(40.dp).width(120.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Primary.copy(alpha = 0.1f) else Color(0xFFF1F5F9),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Primary) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Primary else Color.Gray)
        }
    }
}

@Composable
fun POSProductCard(product: Product, onAdd: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onAdd() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(product.image ?: "☕", fontSize = 40.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                product.name, 
                fontWeight = FontWeight.Bold, 
                fontSize = 14.sp, 
                maxLines = 1, 
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "${String.format("%,.0f", product.price)}đ", 
                color = Primary, 
                fontWeight = FontWeight.Black, 
                fontSize = 15.sp
            )
        }
    }
}
