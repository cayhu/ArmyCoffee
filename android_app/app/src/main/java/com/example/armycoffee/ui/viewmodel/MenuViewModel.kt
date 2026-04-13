package com.example.armycoffee.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.armycoffee.data.api.RetrofitClient
import com.example.armycoffee.data.model.CartItem
import com.example.armycoffee.data.model.Product
import com.example.armycoffee.data.model.Category
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {
    var products by mutableStateOf<List<Product>>(emptyList())
    var categories by mutableStateOf<List<Category>>(emptyList())
    var isLoading by mutableStateOf(false)
    
    // POS State
    var selectedCategoryId by mutableIntStateOf(0)
    var searchText by mutableStateOf("")
    var paymentMethod by mutableStateOf("cash") // cash, qr, ewallet

    private val _cart = mutableStateListOf<CartItem>()
    val cart: List<CartItem> get() = _cart
    
    val cartCount get() = _cart.sumOf { it.quantity }
    val cartTotal get() = _cart.sumOf { it.product.price * it.quantity }

    init {
        loadData()
    }

    fun loadData() {
        isLoading = true
        viewModelScope.launch {
            try {
                // Ưu tiên gọi API từ Server
                val cats = RetrofitClient.instance.getCategories()
                categories = listOf(Category(0, "Tất cả", "🏠")) + cats
                
                val apiProducts = RetrofitClient.instance.getProducts()
                products = apiProducts
            } catch (e: Exception) {
                // Nếu Server lỗi hoặc chưa chạy, dùng tạm dữ liệu ảo để test UI
                categories = listOf(
                    Category(0, "Tất cả", "🏠"),
                    Category(1, "Cà phê", "☕"),
                    Category(2, "Trà", "🍵"),
                    Category(3, "Topping", "🍡"),
                    Category(4, "Ăn vặt", "🍪"),
                    Category(5, "Bánh ngọt", "🍰")
                )
                products = listOf(
                    Product(1, "Cà phê đen", 25000.0, "Cà phê truyền thống", "☕", 1, true),
                    Product(2, "Cà phê sữa", 29000.0, "Cà phê sữa đặc", "☕", 1, true),
                    Product(3, "Trà đào", 35000.0, "Trà đào miếng", "🍑", 2, true),
                    Product(4, "Trà sữa", 40000.0, "Trà sữa chân trâu", "🧋", 2, true),
                    Product(5, "Trân châu trắng", 5000.0, "", "🍡", 3, true),
                    Product(6, "Bánh tráng", 20000.0, "", "🌯", 4, true),
                    Product(7, "Bạc xỉu", 32000.0, "", "☕", 1, true),
                    Product(8, "Bánh Tiramisu", 45000.0, "", "🍰", 5, true)
                )
            } finally {
                isLoading = false
            }
        }
    }

    fun addToCart(product: Product) {
        val existing = _cart.find { it.product.id == product.id }
        if (existing != null) {
            updateQuantity(product.id, 1)
        } else {
            _cart.add(CartItem(product, 1))
        }
    }

    fun updateQuantity(productId: Int, delta: Int) {
        val index = _cart.indexOfFirst { it.product.id == productId }
        if (index != -1) {
            val newItem = _cart[index].copy(quantity = _cart[index].quantity + delta)
            if (newItem.quantity <= 0) {
                _cart.removeAt(index)
            } else {
                _cart[index] = newItem
            }
        }
    }

    fun clearCart() {
        _cart.clear()
    }

    fun checkout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val orderData = mapOf(
                    "items" to _cart.map { 
                        mapOf(
                            "product_id" to it.product.id,
                            "quantity" to it.quantity,
                            "price" to it.product.price
                        )
                    },
                    "total" to cartTotal,
                    "payment_method" to paymentMethod,
                    "cashier" to "Tân" // Có thể lấy từ AuthViewModel sau
                )
                
                RetrofitClient.instance.checkout(orderData)
                _cart.clear()
                onSuccess()
            } catch (e: Exception) {
                // Xử lý lỗi khi không gửi được đơn hàng lên Clever Cloud
                android.util.Log.e("API_ERROR", "Checkout failed", e)
            }
        }
    }
}
