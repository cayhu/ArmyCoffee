package com.army.coffee.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.army.coffee.data.entities.Category
import com.army.coffee.data.entities.CoffeeTable
import com.army.coffee.data.entities.Order
import com.army.coffee.data.entities.OrderItem
import com.army.coffee.data.entities.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Insert
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    fun getProductsByCategory(categoryId: Int): Flow<List<Product>>

    @Insert
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("UPDATE products SET stockQuantity = stockQuantity - :quantity WHERE id = :productId")
    suspend fun decreaseStock(productId: Int, quantity: Int)

    @Query("SELECT * FROM coffee_tables")
    fun getAllTables(): Flow<List<CoffeeTable>>

    @Insert
    suspend fun insertTable(table: CoffeeTable)

    @Update
    suspend fun updateTable(table: CoffeeTable)

    @Delete
    suspend fun deleteTable(table: CoffeeTable)

    @Query("UPDATE coffee_tables SET isOccupied = :isOccupied WHERE id = :tableId")
    suspend fun updateTableStatus(tableId: Int, isOccupied: Boolean)

    @Insert
    suspend fun insertOrder(order: Order): Long

    @Insert
    suspend fun insertOrderItem(orderItem: OrderItem)

    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getItemsForOrder(orderId: Int): List<OrderItem>
}
