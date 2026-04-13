package com.army.coffee.data

import com.army.coffee.data.entities.*
import kotlinx.coroutines.flow.Flow

interface CoffeeRepository {
    fun getAllCategoriesStream(): Flow<List<Category>>
    fun getAllProductsStream(): Flow<List<Product>>
    fun getProductsByCategoryStream(categoryId: Int): Flow<List<Product>>
    fun getAllTablesStream(): Flow<List<CoffeeTable>>
    
    suspend fun insertCategory(category: Category)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    
    suspend fun insertProduct(product: Product)
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(product: Product)
    
    suspend fun insertTable(table: CoffeeTable)
    suspend fun updateTable(table: CoffeeTable)
    suspend fun deleteTable(table: CoffeeTable)
    suspend fun updateTableStatus(tableId: Int, isOccupied: Boolean)
    
    suspend fun createOrder(order: Order, items: List<OrderItem>)
    fun getAllOrdersStream(): Flow<List<Order>>
}
