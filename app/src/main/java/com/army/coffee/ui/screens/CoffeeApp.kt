package com.army.coffee.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.army.coffee.data.entities.*
import com.army.coffee.ui.CoffeeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CoffeeApp(viewModel: CoffeeViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showAdmin) {
        AdminScreen(
            viewModel = viewModel,
            onBack = { viewModel.toggleAdmin(false) }
        )
    } else if (uiState.showHistory) {
        HistoryScreen(
            orders = uiState.orders,
            onBack = { viewModel.toggleHistory(false) }
        )
    } else if (uiState.selectedTable == null) {
        TableSelectionScreen(
            tables = uiState.tables,
            onTableSelected = { viewModel.selectTable(it) },
            onShowHistory = { viewModel.toggleHistory(true) },
            viewModel = viewModel
        )
    } else {
        OrderScreen(viewModel = viewModel)
    }
}

@Composable
fun TableSelectionScreen(
    tables: List<CoffeeTable>, 
    onTableSelected: (CoffeeTable) -> Unit,
    onShowHistory: () -> Unit,
    viewModel: CoffeeViewModel
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Army Coffee", style = MaterialTheme.typography.headlineMedium)
            Row {
                Button(onClick = { viewModel.toggleAdmin(true) }) {
                    Text("Kho")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Button(onClick = onShowHistory) {
                    Text("Lịch sử")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tables) { table ->
                TableItem(table = table, onClick = { onTableSelected(table) })
            }
        }
    }
}

@Composable
fun TableItem(table: CoffeeTable, onClick: () -> Unit) {
    Card(
        modifier = Modifier.size(100.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (table.isOccupied) Color(0xFFFFCDD2) else Color(0xFFC8E6C9)
        )
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(table.name, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OrderScreen(viewModel: CoffeeViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showCartDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.selectTable(null) }) {
                Text("<", style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Bàn: ${uiState.selectedTable?.name}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                if (uiState.selectedTable?.isOccupied == true) {
                    Text("Đang có khách", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            if (uiState.selectedTable?.isOccupied == true) {
                Button(
                    onClick = { 
                        viewModel.updateTableStatus(uiState.selectedTable!!.id, false)
                        viewModel.selectTable(null)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Trả bàn")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Category Selector
        LazyRow {
            items(uiState.categories) { category ->
                CategoryChip(
                    category = category,
                    isSelected = category.id == uiState.selectedCategoryId,
                    onClick = { viewModel.selectCategory(category.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Product List
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(uiState.products) { product ->
                ProductItem(
                    product = product, 
                    onAddToCart = { viewModel.addToCart(product) }
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        // Cart Summary
        if (uiState.cart.isNotEmpty()) {
            CartSummary(
                cart = uiState.cart, 
                onShowDetails = { showCartDialog = true },
                onCheckout = { 
                    viewModel.checkout()
                    showCartDialog = false
                }
            )
        }
    }

    if (showCartDialog) {
        CartDetailDialog(
            cart = uiState.cart,
            onDismiss = { showCartDialog = false },
            onIncrease = { viewModel.addToCart(it) },
            onDecrease = { viewModel.removeFromCart(it) },
            onCheckout = {
                viewModel.checkout()
                showCartDialog = false
            }
        )
    }
}

@Composable
fun ProductItem(product: Product, onAddToCart: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text("${product.price} VND", style = MaterialTheme.typography.bodyMedium)
                Text("Kho: ${product.stockQuantity}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Button(
                onClick = onAddToCart,
                enabled = product.stockQuantity > 0
            ) {
                Text(if (product.stockQuantity > 0) "Thêm" else "Hết hàng")
            }
        }
    }
}

@Composable
fun HistoryScreen(orders: List<Order>, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onBack) {
                Text("<")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Lịch sử đơn hàng", style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(orders) { order ->
                OrderHistoryItem(order = order)
            }
        }
    }
}

@Composable
fun OrderHistoryItem(order: Order) {
    val sdf = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
    val dateStr = sdf.format(Date(order.timestamp))
    
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(order.tableName, fontWeight = FontWeight.Bold)
                Text("${order.totalAmount} VND", color = MaterialTheme.colorScheme.primary)
            }
            Text(dateStr, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun CategoryChip(category: Category, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.padding(end = 8.dp).clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = category.name,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CartSummary(cart: Map<Product, Int>, onShowDetails: () -> Unit, onCheckout: () -> Unit) {
    val total = cart.entries.sumOf { it.key.price * it.value }
    val itemCount = cart.values.sum()
    
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onShowDetails() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Giỏ hàng ($itemCount món)", fontWeight = FontWeight.Bold)
                Text("Tổng: $total VND", style = MaterialTheme.typography.titleMedium)
            }
            Button(onClick = onCheckout) {
                Text("Thanh toán")
            }
        }
    }
}

@Composable
fun CartDetailDialog(
    cart: Map<Product, Int>,
    onDismiss: () -> Unit,
    onIncrease: (Product) -> Unit,
    onDecrease: (Product) -> Unit,
    onCheckout: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chi tiết đơn hàng") },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(cart.entries.toList()) { (product, quantity) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontWeight = FontWeight.Bold)
                            Text("${product.price * quantity} VND")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onDecrease(product) }) {
                                Text("-", style = MaterialTheme.typography.titleLarge)
                            }
                            Text("$quantity", modifier = Modifier.padding(horizontal = 8.dp))
                            IconButton(onClick = { onIncrease(product) }) {
                                Text("+", style = MaterialTheme.typography.titleLarge)
                            }
                        }
                    }
                }
                item {
                    val total = cart.entries.sumOf { it.key.price * it.value }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Tổng cộng: $total VND", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(onClick = onCheckout) { Text("Xác nhận đặt món") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Đóng") }
        }
    )
}
