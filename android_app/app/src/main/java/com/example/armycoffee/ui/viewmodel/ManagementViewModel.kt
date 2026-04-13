package com.example.armycoffee.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.armycoffee.data.api.RetrofitClient
import com.example.armycoffee.data.model.Product
import com.example.armycoffee.data.model.Category
import com.example.armycoffee.data.model.Employee
import kotlinx.coroutines.launch

class ManagementViewModel : ViewModel() {
    // Product State
    var products = mutableStateListOf<Product>()
    var categories = mutableStateListOf<Category>()
    var employees = mutableStateListOf<Employee>()
    
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        loadAllData()
    }

    fun loadAllData() {
        isLoading = true
        viewModelScope.launch {
            try {
                loadCategories()
                loadProducts()
                loadEmployees()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getProducts()
                products.clear()
                products.addAll(response.map { apiProduct ->
                    val categoryName = categories.find { it.id == apiProduct.category_id }?.name ?: "Khác"
                    Product(
                        id = apiProduct.id,
                        name = apiProduct.name,
                        price = apiProduct.price,
                        description = "",
                        image = apiProduct.image ?: "☕",
                        category_id = apiProduct.category_id,
                        is_available = true,
                        categoryName = categoryName
                    )
                })
            } catch (e: Exception) {
                // Fallback to mock data if API fails
                products.clear()
                products.addAll(listOf(
                    Product(1, "Cà phê Sữa đá", 29000.0, "", "☕", 1, true, "Cà phê"),
                    Product(2, "Bạc xỉu", 35000.0, "", "☕", 1, true, "Cà phê"),
                    Product(3, "Trà Đào Cam Sả", 45000.0, "", "🍑", 2, true, "Trà trái cây"),
                    Product(4, "Matcha Latte", 49000.0, "", "🍵", 3, true, "Đồ uống nóng"),
                    Product(5, "Bánh Mì Pate", 25000.0, "", "🥖", 4, true, "Thức ăn")
                ))
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getCategories()
                categories.clear()
                categories.addAll(response.map { Category(it.id, it.name, it.icon, true) })
            } catch (e: Exception) {
                categories.clear()
                categories.addAll(listOf(
                    Category(1, "Cà phê", "☕", true),
                    Category(2, "Trà trái cây", "🍑", true),
                    Category(3, "Đồ uống nóng", "🍵", true),
                    Category(4, "Thức ăn", "🥖", true),
                    Category(5, "Bánh ngọt", "🍰", true)
                ))
            }
        }
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getEmployees()
                employees.clear()
                employees.addAll(response)
            } catch (e: Exception) {
                errorMessage = "Không thể lấy danh sách nhân viên: ${e.message}"
            }
        }
    }

    fun addEmployee(name: String, role: String, email: String, status: String = "Đang làm việc") {
        viewModelScope.launch {
            try {
                val newEmployee = RetrofitClient.instance.addEmployee(
                    mapOf("name" to name, "role" to role, "email" to email, "status" to status)
                )
                employees.add(newEmployee)
            } catch (e: Exception) {
                errorMessage = "Lỗi thêm nhân viên: ${e.message}"
            }
        }
    }

    fun deleteEmployee(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.deleteEmployee(id)
                employees.removeIf { it.id == id }
            } catch (e: Exception) {
                errorMessage = "Lỗi xóa nhân viên: ${e.message}"
            }
        }
    }

    fun updateEmployee(id: Int, name: String, role: String, email: String, status: String) {
        val index = employees.indexOfFirst { it.id == id }
        if (index != -1) {
            employees[index] = Employee(id, name, role, email, status)
        }
    }

    // Product Management
    fun addProduct(name: String, categoryId: Int, price: Double, image: String) {
        viewModelScope.launch {
            try {
                val newProductApi = RetrofitClient.instance.addProduct(mapOf(
                    "name" to name,
                    "category_id" to categoryId,
                    "price" to price,
                    "image" to image,
                    "is_available" to true
                ))
                val categoryName = categories.find { it.id == categoryId }?.name ?: ""
                products.add(Product(newProductApi.id, name, price, "", image, categoryId, true, categoryName))
            } catch (e: Exception) {
                errorMessage = "Lỗi thêm sản phẩm: ${e.message}"
            }
        }
    }

    fun updateProduct(id: Int, name: String, categoryId: Int, price: Double, image: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.updateProduct(id, mapOf(
                    "name" to name,
                    "category_id" to categoryId,
                    "price" to price,
                    "image" to image,
                    "is_available" to true
                ))
                val index = products.indexOfFirst { it.id == id }
                if (index != -1) {
                    val categoryName = categories.find { it.id == categoryId }?.name ?: ""
                    products[index] = Product(id, name, price, "", image, categoryId, true, categoryName)
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi cập nhật sản phẩm: ${e.message}"
            }
        }
    }

    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.deleteProduct(id)
                products.removeIf { it.id == id }
            } catch (e: Exception) {
                errorMessage = "Lỗi xóa sản phẩm: ${e.message}"
            }
        }
    }

    // Category Management
    fun addCategory(name: String, icon: String) {
        viewModelScope.launch {
            try {
                val newCategoryApi = RetrofitClient.instance.addCategory(
                    mapOf("name" to name, "icon" to icon)
                )
                // Map API Category to UI Category
                categories.add(Category(newCategoryApi.id, newCategoryApi.name, newCategoryApi.icon, true))
            } catch (e: Exception) {
                errorMessage = "Lỗi thêm danh mục: ${e.message}"
            }
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.deleteCategory(id)
                categories.removeIf { it.id == id }
            } catch (e: Exception) {
                errorMessage = "Lỗi xóa danh mục: ${e.message}"
            }
        }
    }
}
