package com.army.coffee.data

import com.army.coffee.data.dao.CoffeeDao
import com.army.coffee.data.entities.*
import kotlinx.coroutines.flow.Flow

class OfflineCoffeeRepository(private val coffeeDao: CoffeeDao) : CoffeeRepository {
    fun getAllCategoriesStream(): Flow<List<Category>> = coffeeDao.getAllCategories()

    fun getAllProductsStream(): Flow<List<Product>> = coffeeDao.getAllProducts()

    fun getProductsByCategoryStream(categoryId: Int): Flow<List<Product>> =
        coffeeDao.getProductsByCategory(categoryId)

    fun getAllTablesStream(): Flow<List<CoffeeTable>> = coffeeDao.getAllTables()

    suspend fun insertCategory(category: Category) = coffeeDao.insertCategory(category)

    suspend fun updateCategory(category: Category) = coffeeDao.updateCategory(category)

    suspend fun deleteCategory(category: Category) = coffeeDao.deleteCategory(category)

    suspend fun insertProduct(product: Product) = coffeeDao.insertProduct(product)

    suspend fun updateProduct(product: Product) = coffeeDao.updateProduct(product)

    suspend fun deleteProduct(product: Product) = coffeeDao.deleteProduct(product)

    suspend fun insertTable(table: CoffeeTable) = coffeeDao.insertTable(table)

    suspend fun updateTable(table: CoffeeTable) = coffeeDao.updateTable(table)

    suspend fun deleteTable(table: CoffeeTable) = coffeeDao.deleteTable(table)

    suspend fun updateTableStatus(tableId: Int, isOccupied: Boolean) = 
        coffeeDao.updateTableStatus(tableId, isOccupied)

    suspend fun createOrder(order: Order, items: List<OrderItem>) {
        val orderId = coffeeDao.insertOrder(order).toInt()
        items.forEach { item ->
            val itemWithOrderId = item.copy(orderId = orderId)
            coffeeDao.insertOrderItem(itemWithOrderId)
            coffeeDao.decreaseStock(item.productId, item.quantity)
        }
    }

    fun getAllOrdersStream(): Flow<List<Order>> = coffeeDao.getAllOrders()
}
