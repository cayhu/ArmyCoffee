package com.army.coffee.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.army.coffee.data.CoffeeRepository
import com.army.coffee.data.entities.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class Staff(
    val id: Int,
    val username: String,
    val fullName: String,
    val role: String
)

data class CoffeeUiState(
    val categories: List<Category> = emptyList(),
    val products: List<Product> = emptyList(),
    val tables: List<CoffeeTable> = emptyList(),
    val orders: List<Order> = emptyList(),
    val staffs: List<Staff> = emptyList(),
    val selectedCategoryId: Int? = null,
    val selectedTable: CoffeeTable? = null,
    val cart: Map<Product, Int> = emptyMap(),
    val showHistory: Boolean = false,
    val showAdmin: Boolean = false,
    val currentStaff: Staff? = null,
    val isAdminAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CoffeeViewModel(private val repository: CoffeeRepository) : ViewModel() {

    private val ADMIN_PASSWORD = "1234"
    private val _uiState = MutableStateFlow(CoffeeUiState())
    val uiState: StateFlow<CoffeeUiState> = _uiState.asStateFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                repository.getAllCategoriesStream().collectLatest { categories ->
                    _uiState.value = _uiState.value.copy(categories = categories)
                    if (categories.isNotEmpty() && _uiState.value.selectedCategoryId == null) {
                        selectCategory(categories[0].id)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Không thể tải danh mục: ${e.message}")
            }
        }
        
        viewModelScope.launch {
            try {
                repository.getAllTablesStream().collectLatest { tables ->
                    _uiState.value = _uiState.value.copy(tables = tables)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Không thể tải danh sách bàn: ${e.message}")
            }
        }

        viewModelScope.launch {
            try {
                repository.getAllOrdersStream().collectLatest { orders ->
                    _uiState.value = _uiState.value.copy(orders = orders)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Không thể tải lịch sử đơn hàng: ${e.message}")
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun login(username: String, password: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                // In a real app, we'd call repository.login(username, password)
                // For now, let's assume we've added this to the repository
                // and it returns a Staff object
                // val staff = repository.login(username, password)
                // _uiState.value = _uiState.value.copy(currentStaff = staff, isLoading = false)
                
                // Mocking login for now if repository doesn't have it yet
                if (username == "admin" && password == "1234") {
                    _uiState.value = _uiState.value.copy(
                        currentStaff = Staff(id = 1, username = "admin", fullName = "Quản trị viên", role = "ADMIN"),
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Sai tài khoản hoặc mật khẩu")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Lỗi đăng nhập: ${e.message}")
            }
        }
    }

    fun logout() {
        _uiState.value = _uiState.value.copy(currentStaff = null, showAdmin = false, showHistory = false, isAdminAuthenticated = false)
    }

    fun authenticateAdmin(password: String): Boolean {
        return if (password == ADMIN_PASSWORD) {
            _uiState.value = _uiState.value.copy(isAdminAuthenticated = true)
            true
        } else {
            false
        }
    }

    fun logoutAdmin() {
        _uiState.value = _uiState.value.copy(isAdminAuthenticated = false)
    }

    fun toggleHistory(show: Boolean) {
        _uiState.value = _uiState.value.copy(showHistory = show)
        if (show) refreshData()
    }

    fun toggleAdmin(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAdmin = show)
        if (show) refreshData()
    }

    fun addProduct(name: String, price: Double, categoryId: Int, stock: Int) {
        viewModelScope.launch {
            repository.insertProduct(Product(name = name, price = price, categoryId = categoryId, stockQuantity = stock))
            refreshData()
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
            refreshData()
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            refreshData()
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            repository.insertCategory(Category(name = name))
            refreshData()
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
            refreshData()
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            refreshData()
        }
    }

    fun addTable(name: String) {
        viewModelScope.launch {
            repository.insertTable(CoffeeTable(name = name))
            refreshData()
        }
    }

    fun updateTable(table: CoffeeTable) {
        viewModelScope.launch {
            repository.updateTable(table)
            refreshData()
        }
    }

    fun deleteTable(table: CoffeeTable) {
        viewModelScope.launch {
            repository.deleteTable(table)
            refreshData()
        }
    }

    fun updateTableStatus(tableId: Int, isOccupied: Boolean) {
        viewModelScope.launch {
            repository.updateTableStatus(tableId, isOccupied)
            refreshData()
        }
    }

    fun selectTable(table: CoffeeTable?) {
        _uiState.value = _uiState.value.copy(selectedTable = table)
    }

    fun selectCategory(categoryId: Int) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
        viewModelScope.launch {
            repository.getProductsByCategoryStream(categoryId).collectLatest { products ->
                _uiState.value = _uiState.value.copy(products = products)
            }
        }
    }

    fun addToCart(product: Product) {
        if (product.stockQuantity <= 0) return
        val currentCart = _uiState.value.cart.toMutableMap()
        currentCart[product] = (currentCart[product] ?: 0) + 1
        _uiState.value = _uiState.value.copy(cart = currentCart)
    }

    fun removeFromCart(product: Product) {
        val currentCart = _uiState.value.cart.toMutableMap()
        val count = currentCart[product] ?: 0
        if (count > 1) {
            currentCart[product] = count - 1
        } else {
            currentCart.remove(product)
        }
        _uiState.value = _uiState.value.copy(cart = currentCart)
    }

    fun clearCart() {
        _uiState.value = _uiState.value.copy(cart = emptyMap())
    }

    fun checkout() {
        val table = _uiState.value.selectedTable ?: return
        val cart = _uiState.value.cart
        if (cart.isEmpty()) return

        val totalAmount = cart.entries.sumOf { it.key.price * it.value }
        
        viewModelScope.launch {
            val order = Order(
                tableId = table.id,
                tableName = table.name,
                totalAmount = totalAmount
            )
            
            val orderItems = cart.map { (product, quantity) ->
                OrderItem(
                    orderId = 0,
                    productId = product.id,
                    productName = product.name,
                    quantity = quantity,
                    price = product.price
                )
            }
            
            repository.createOrder(order, orderItems)
            // No need to manual update table status as server handles it
            _uiState.value = _uiState.value.copy(cart = emptyMap(), selectedTable = null)
            refreshData()
        }
    }
}
