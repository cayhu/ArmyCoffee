package com.army.coffee.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.army.coffee.data.OfflineCoffeeRepository
import com.army.coffee.data.entities.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class CoffeeUiState(
    val categories: List<Category> = emptyList(),
    val products: List<Product> = emptyList(),
    val tables: List<CoffeeTable> = emptyList(),
    val orders: List<Order> = emptyList(),
    val selectedCategoryId: Int? = null,
    val selectedTable: CoffeeTable? = null,
    val cart: Map<Product, Int> = emptyMap(),
    val showHistory: Boolean = false,
    val showAdmin: Boolean = false,
    val isAdminAuthenticated: Boolean = false
)

class CoffeeViewModel(private val repository: OfflineCoffeeRepository) : ViewModel() {

    private val ADMIN_PASSWORD = "1234"
    private val _uiState = MutableStateFlow(CoffeeUiState())
    val uiState: StateFlow<CoffeeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllCategoriesStream().collectLatest { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
                if (categories.isNotEmpty() && _uiState.value.selectedCategoryId == null) {
                    selectCategory(categories[0].id)
                }
            }
        }
        
        viewModelScope.launch {
            repository.getAllTablesStream().collectLatest { tables ->
                _uiState.value = _uiState.value.copy(tables = tables)
            }
        }

        viewModelScope.launch {
            repository.getAllOrdersStream().collectLatest { orders ->
                _uiState.value = _uiState.value.copy(orders = orders)
            }
        }
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
        _uiState.value = _uiState.value.copy(isAdminAuthenticated = false, showAdmin = false)
    }

    fun toggleHistory(show: Boolean) {
        _uiState.value = _uiState.value.copy(showHistory = show)
    }

    fun toggleAdmin(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAdmin = show)
    }

    fun addProduct(name: String, price: Double, categoryId: Int, stock: Int) {
        viewModelScope.launch {
            repository.insertProduct(Product(name = name, price = price, categoryId = categoryId, stockQuantity = stock))
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            repository.insertCategory(Category(name = name))
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun addTable(name: String) {
        viewModelScope.launch {
            repository.insertTable(CoffeeTable(name = name))
        }
    }

    fun updateTable(table: CoffeeTable) {
        viewModelScope.launch {
            repository.updateTable(table)
        }
    }

    fun deleteTable(table: CoffeeTable) {
        viewModelScope.launch {
            repository.deleteTable(table)
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
            repository.updateTableStatus(table.id, true)
            _uiState.value = _uiState.value.copy(cart = emptyMap(), selectedTable = null)
        }
    }
}
