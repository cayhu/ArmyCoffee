package com.army.coffee.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.army.coffee.data.dao.CoffeeDao
import com.army.coffee.data.entities.Category
import com.army.coffee.data.entities.CoffeeTable
import com.army.coffee.data.entities.Order
import com.army.coffee.data.entities.OrderItem
import com.army.coffee.data.entities.Product

@Database(entities = [Category::class, Product::class, CoffeeTable::class, Order::class, OrderItem::class], version = 3, exportSchema = false)
abstract class CoffeeDatabase : RoomDatabase() {
    abstract fun coffeeDao(): CoffeeDao

    companion object {
        @Volatile
        private var Instance: CoffeeDatabase? = null

        fun getDatabase(context: Context): CoffeeDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CoffeeDatabase::class.java, "coffee_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
