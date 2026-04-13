package com.army.coffee.data

import com.army.coffee.data.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NetworkCoffeeRepository(private val apiService: CoffeeApiService) : CoffeeRepository {
    
    override fun getAllCategoriesStream(): Flow<List<Category>> = flow {
        emit(apiService.getCategories())
    }

    override fun getAllProductsStream(): Flow<List<Product>> = flow {
        emit(apiService.getProducts())
    }

    override fun getProductsByCategoryStream(categoryId: Int): Flow<List<Product>> = flow {
        val allProducts = apiService.getProducts()
        emit(allProducts.filter { it.categoryId == categoryId })
    }

    override fun getAllTablesStream(): Flow<List<CoffeeTable>> = flow {
        emit(apiService.getTables())
    }

    override suspend fun insertCategory(category: Category) {
        apiService.createCategory(category)
    }

    override suspend fun updateCategory(category: Category) {
        apiService.updateCategory(category.id, category)
    }

    override suspend fun deleteCategory(category: Category) {
        apiService.deleteCategory(category.id)
    }

    override suspend fun insertProduct(product: Product) {
        apiService.createProduct(product)
    }

    override suspend fun updateProduct(product: Product) {
        apiService.updateProduct(product.id, product)
    }

    override suspend fun deleteProduct(product: Product) {
        apiService.deleteProduct(product.id)
    }

    override suspend fun insertTable(table: CoffeeTable) {
        apiService.createTable(table)
    }

    override suspend fun updateTable(table: CoffeeTable) {
        // Implement if needed
    }

    override suspend fun deleteTable(table: CoffeeTable) {
        apiService.deleteTable(table.id)
    }

    override suspend fun updateTableStatus(tableId: Int, isOccupied: Boolean) {
        apiService.updateTableStatus(tableId, mapOf("isOccupied" to isOccupied))
    }

    override suspend fun createOrder(order: Order, items: List<OrderItem>) {
        val request = OrderRequest(
            tableId = order.tableId,
            tableName = order.tableName,
            totalAmount = order.totalAmount,
            items = items.map { OrderItemRequest(it.productId, it.productName, it.quantity, it.price) }
        )
        apiService.createOrder(request)
    }

    override fun getAllOrdersStream(): Flow<List<Order>> = flow {
        emit(apiService.getOrders())
    }
}
