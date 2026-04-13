package com.example.armycoffee.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.armycoffee.data.model.Employee
import com.example.armycoffee.ui.theme.Primary

import androidx.compose.material.icons.filled.Warning
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.armycoffee.ui.viewmodel.ManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: ManagementViewModel = viewModel()
) {
    var showDeleteConfirm by remember { mutableStateOf<Employee?>(null) }
    var showEmployeeDialog by remember { mutableStateOf<Employee?>(null) } // null = hide, Employee(id=-1) = add new
    var isAddingNew by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý Nhân viên", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        isAddingNew = true
                        showEmployeeDialog = Employee(-1, "", "STAFF", "", "Đang làm việc")
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF3F4F6)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(viewModel.employees, key = { it.id }) { employee ->
                EmployeeItemCard(
                    employee = employee,
                    onEdit = { 
                        isAddingNew = false
                        showEmployeeDialog = employee 
                    },
                    onDelete = { showDeleteConfirm = employee }
                )
            }
        }
    }

    // Add/Edit Dialog
    showEmployeeDialog?.let { employee ->
        EmployeeEditDialog(
            employee = employee,
            isNew = isAddingNew,
            onDismiss = { showEmployeeDialog = null },
            onConfirm = { name, role, email ->
                if (isAddingNew) {
                    viewModel.addEmployee(name, role, email)
                } else {
                    viewModel.updateEmployee(employee.id, name, role, email, employee.status)
                }
                showEmployeeDialog = null
            }
        )
    }

    showDeleteConfirm?.let { employee ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red) },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc muốn xóa nhân viên '${employee.name}'?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteEmployee(employee.id)
                    showDeleteConfirm = null
                }) {
                    Text("Xóa", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeEditDialog(
    employee: Employee,
    isNew: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var name by remember(employee) { mutableStateOf(employee.name) }
    var email by remember(employee) { mutableStateOf(employee.email) }
    var role by remember(employee) { mutableStateOf(employee.role) }
    var expanded by remember { mutableStateOf(false) }
    val roles = listOf("ADMIN", "STAFF", "PHA CHE", "KHO")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "Thêm nhân viên" else "Sửa thông tin") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Họ tên") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Chức vụ (Phân quyền)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        roles.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r) },
                                onClick = {
                                    role = r
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, role, email) }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

/*
data class Employee(
    val id: Int,
    val name: String,
    val role: String,
    val email: String,
    val status: String
)
*/

@Composable
fun EmployeeItemCard(
    employee: Employee,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Primary)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(employee.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${employee.role} • ${employee.status}", fontSize = 12.sp, color = Color.Gray)
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Sửa thông tin") },
                        onClick = { showMenu = false; onEdit() }
                    )
                    DropdownMenuItem(
                        text = { Text("Xóa nhân viên", color = Color.Red) },
                        onClick = { showMenu = false; onDelete() }
                    )
                }
            }
        }
    }
}
