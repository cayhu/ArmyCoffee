package com.army.coffee.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.army.coffee.data.entities.Category
import com.army.coffee.data.entities.CoffeeTable
import com.army.coffee.data.entities.Product
import com.army.coffee.ui.CoffeeViewModel

@Composable
fun AdminScreen(viewModel: CoffeeViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    
    var showAddProductDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    
    var showAddTableDialog by remember { mutableStateOf(false) }
    var editingTable by remember { mutableStateOf<CoffeeTable?>(null) }
    
    var passwordInput by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }

    if (!uiState.isAdminAuthenticated) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Yêu cầu quyền Admin", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = passwordInput,
                onValueChange = { passwordInput = it; loginError = false },
                label = { Text("Mật khẩu") },
                isError = loginError,
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
            )
            if (loginError) {
                Text("Sai mật khẩu!", color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(onClick = onBack) { Text("Hủy") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (viewModel.authenticateAdmin(passwordInput)) {
                        passwordInput = ""
                    } else {
                        loginError = true
                    }
                }) {
                    Text("Đăng nhập")
                }
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            SmallTopAppBar(
                title = { Text("Quản trị Army Coffee") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.logoutAdmin() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Đăng xuất")
                    }
                }
            )

            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Sản phẩm", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Danh mục", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                    Text("Bàn", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }) {
                    Text("Thống kê", modifier = Modifier.padding(16.dp))
                }
            }

            Box(modifier = Modifier.weight(1f).padding(16.dp)) {
                when (selectedTab) {
                    0 -> ProductManagement(
                        products = uiState.products,
                        onAdd = { showAddProductDialog = true },
                        onEdit = { editingProduct = it },
                        onDelete = { viewModel.deleteProduct(it) }
                    )
                    1 -> CategoryManagement(
                        categories = uiState.categories,
                        onAdd = { showAddCategoryDialog = true },
                        onEdit = { editingCategory = it },
                        onDelete = { viewModel.deleteCategory(it) }
                    )
                    2 -> TableManagement(
                        tables = uiState.tables,
                        onAdd = { showAddTableDialog = true },
                        onEdit = { editingTable = it },
                        onDelete = { viewModel.deleteTable(it) }
                    )
                    3 -> AnalyticsSection(
                        totalRevenue = uiState.orders.sumOf { it.totalAmount },
                        totalOrders = uiState.orders.size
                    )
                }
            }
        }

        // Dialogs
        if (showAddProductDialog || editingProduct != null) {
            ProductDialog(
                product = editingProduct,
                categories = uiState.categories,
                onDismiss = { showAddProductDialog = false; editingProduct = null },
                onConfirm = { name, price, catId, stock ->
                    if (editingProduct != null) {
                        viewModel.updateProduct(editingProduct!!.copy(name = name, price = price, categoryId = catId, stockQuantity = stock))
                    } else {
                        viewModel.addProduct(name, price, catId, stock)
                    }
                    showAddProductDialog = false
                    editingProduct = null
                }
            )
        }

        if (showAddCategoryDialog || editingCategory != null) {
            CategoryDialog(
                category = editingCategory,
                onDismiss = { showAddCategoryDialog = false; editingCategory = null },
                onConfirm = { name ->
                    if (editingCategory != null) {
                        viewModel.updateCategory(editingCategory!!.copy(name = name))
                    } else {
                        viewModel.addCategory(name)
                    }
                    showAddCategoryDialog = false
                    editingCategory = null
                }
            )
        }

        if (showAddTableDialog || editingTable != null) {
            TableDialog(
                table = editingTable,
                onDismiss = { showAddTableDialog = false; editingTable = null },
                onConfirm = { name ->
                    if (editingTable != null) {
                        viewModel.updateTable(editingTable!!.copy(name = name))
                    } else {
                        viewModel.addTable(name)
                    }
                    showAddTableDialog = false
                    editingTable = null
                }
            )
        }
    }
}

@Composable
fun ProductManagement(products: List<Product>, onAdd: () -> Unit, onEdit: (Product) -> Unit, onDelete: (Product) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Danh sách món", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onAdd) { Text("+ Thêm") }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(products) { product ->
                AdminItemRow(
                    title = product.name,
                    subtitle = "${product.price} VND - Tồn kho: ${product.stockQuantity}",
                    onEdit = { onEdit(product) },
                    onDelete = { onDelete(product) }
                )
            }
        }
    }
}

@Composable
fun CategoryManagement(categories: List<Category>, onAdd: () -> Unit, onEdit: (Category) -> Unit, onDelete: (Category) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Danh mục món", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onAdd) { Text("+ Thêm") }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(categories) { category ->
                AdminItemRow(
                    title = category.name,
                    subtitle = "ID: ${category.id}",
                    onEdit = { onEdit(category) },
                    onDelete = { onDelete(category) }
                )
            }
        }
    }
}

@Composable
fun TableManagement(tables: List<CoffeeTable>, onAdd: () -> Unit, onEdit: (CoffeeTable) -> Unit, onDelete: (CoffeeTable) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Danh sách bàn", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onAdd) { Text("+ Thêm") }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(tables) { table ->
                AdminItemRow(
                    title = table.name,
                    subtitle = if (table.isOccupied) "Đang có khách" else "Trống",
                    onEdit = { onEdit(table) },
                    onDelete = { onDelete(table) }
                )
            }
        }
    }
}

@Composable
fun AdminItemRow(title: String, subtitle: String, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AnalyticsSection(totalRevenue: Double, totalOrders: Int) {
    Column {
        Text("Thống kê doanh thu", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Tổng doanh thu", style = MaterialTheme.typography.labelLarge)
                Text("$totalRevenue VND", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tổng đơn hàng: $totalOrders", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun ProductDialog(product: Product?, categories: List<Category>, onDismiss: () -> Unit, onConfirm: (String, Double, Int, Int) -> Unit) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var price by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var stock by remember { mutableStateOf(product?.stockQuantity?.toString() ?: "0") }
    var selectedCatId by remember { mutableStateOf(product?.categoryId ?: categories.firstOrNull()?.id ?: 1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Thêm Sản phẩm" else "Sửa Sản phẩm") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Tên món") })
                TextField(value = price, onValueChange = { price = it }, label = { Text("Giá tiền") })
                TextField(value = stock, onValueChange = { stock = it }, label = { Text("Số lượng kho") })
                
                Text("Danh mục:", modifier = Modifier.padding(top = 8.dp))
                categories.forEach { category ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable { selectedCatId = category.id }.padding(vertical = 4.dp)
                    ) {
                        RadioButton(selected = selectedCatId == category.id, onClick = { selectedCatId = category.id })
                        Text(category.name)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { 
                if (name.isNotEmpty() && price.isNotEmpty()) {
                    onConfirm(name, price.toDoubleOrNull() ?: 0.0, selectedCatId, stock.toIntOrNull() ?: 0)
                }
            }) {
                Text(if (product == null) "Thêm" else "Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
fun CategoryDialog(category: Category?, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (category == null) "Thêm Danh mục" else "Sửa Danh mục") },
        text = { TextField(value = name, onValueChange = { name = it }, label = { Text("Tên danh mục") }) },
        confirmButton = {
            Button(onClick = { if (name.isNotEmpty()) onConfirm(name) }) {
                Text(if (category == null) "Thêm" else "Lưu")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}

@Composable
fun TableDialog(table: CoffeeTable?, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf(table?.name ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (table == null) "Thêm Bàn" else "Sửa Bàn") },
        text = { TextField(value = name, onValueChange = { name = it }, label = { Text("Tên bàn (vd: Bàn 1)") }) },
        confirmButton = {
            Button(onClick = { if (name.isNotEmpty()) onConfirm(name) }) {
                Text(if (table == null) "Thêm" else "Lưu")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}
