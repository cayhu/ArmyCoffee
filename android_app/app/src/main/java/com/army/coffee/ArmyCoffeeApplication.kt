package com.army.coffee

import android.app.Application
import com.army.coffee.data.AppContainer
import com.army.coffee.data.AppDataContainer
import com.army.coffee.data.entities.Category
import com.army.coffee.data.entities.CoffeeTable
import com.army.coffee.data.entities.Product
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ArmyCoffeeApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        
        // Populate database with some initial data
        MainScope().launch {
            val repository = container.coffeeRepository
            val categories = repository.getAllCategoriesStream().first()
            if (categories.isEmpty()) {
                repository.insertCategory(Category(name = "Coffee"))
                repository.insertCategory(Category(name = "Tea"))
                repository.insertCategory(Category(name = "Snacks"))
                
                // Assuming IDs will be 1, 2, 3
                repository.insertProduct(Product(name = "Army Espresso", price = 30000.0, categoryId = 1))
                repository.insertProduct(Product(name = "Army Latte", price = 45000.0, categoryId = 1))
                repository.insertProduct(Product(name = "Army Peach Tea", price = 35000.0, categoryId = 2))
                repository.insertProduct(Product(name = "Army Croissant", price = 25000.0, categoryId = 3))
            }
            
            val tables = repository.getAllTablesStream().first()
            if (tables.isEmpty()) {
                for (i in 1..12) {
                    repository.insertTable(CoffeeTable(name = "Bàn $i"))
                }
            }
        }
    }
}
