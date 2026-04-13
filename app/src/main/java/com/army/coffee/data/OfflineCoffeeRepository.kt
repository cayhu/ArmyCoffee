package com.army.coffee.data

import com.army.coffee.data.dao.CoffeeDao
import com.army.coffee.data.entities.*
import kotlinx.coroutines.flow.Flow

class OfflineCoffeeRepository(private val coffeeDao: CoffeeDao) : CoffeeRepository {
    override fun getAllCategoriesStream(): Flow<List<Category>> = coffeeDao.getAllCategories()

    override fun getAllProductsStream(): Flow<List<Product>> = coffeeDao.getAllProducts()

    override fun getProductsByCategoryStream(categoryId: Int): Flow<List<Product>> =
        coffeeDao.getProductsByCategory(categoryId)

    override fun getAllTablesStream(): Flow<List<CoffeeTable>> = coffeeDao.getAllTables()

    override suspend fun insertCategory(category: Category) = coffeeDao.insertCategory(category)

    override suspend fun updateCategory(category: Category) = coffeeDao.updateCategory(category)

    override suspend fun deleteCategory(category: Category) = coffeeDao.deleteCategory(category)

    override suspend fun insertProduct(product: Product) = coffeeDao.insertProduct(product)

    override suspend fun updateProduct(product: Product) = coffeeDao.updateProduct(product)

    override suspend fun deleteProduct(product: Product) = coffeeDao.deleteProduct(product)

    override suspend fun insertTable(table: CoffeeTable) = coffeeDao.insertTable(table)

    override suspend fun updateTable(table: CoffeeTable) = coffeeDao.updateTable(table)

    override suspend fun deleteTable(table: CoffeeTable) = coffeeDao.deleteTable(table)

    override suspend fun updateTableStatus(tableId: Int, isOccupied: Boolean) = 
        coffeeDao.updateTableStatus(tableId, isOccupied)

    override suspend fun createOrder(order: Order, items: List<OrderItem>) {
        val orderId = coffeeDao.insertOrder(order).toInt()
        items.forEach { item ->
            val itemWithOrderId = item.copy(orderId = orderId)
            coffeeDao.insertOrderItem(itemWithOrderId)
            coffeeDao.decreaseStock(item.productId, item.quantity)
        }
    }

    override fun getAllOrdersStream(): Flow<List<Order>> = coffeeDao.getAllOrders()
}
