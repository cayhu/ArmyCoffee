package com.army.coffee.data

import android.content.Context

interface AppContainer {
    val coffeeRepository: OfflineCoffeeRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val coffeeRepository: OfflineCoffeeRepository by lazy {
        OfflineCoffeeRepository(CoffeeDatabase.getDatabase(context).coffeeDao())
    }
}
