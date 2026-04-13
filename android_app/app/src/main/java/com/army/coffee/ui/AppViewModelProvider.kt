package com.army.coffee.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.army.coffee.ArmyCoffeeApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            // Đổi sang networkRepository để dùng dữ liệu Cloud, 
            // hoặc giữ coffeeRepository để dùng SQLite local
            CoffeeViewModel(armyCoffeeApplication().container.networkRepository)
        }
    }
}

fun CreationExtras.armyCoffeeApplication(): ArmyCoffeeApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ArmyCoffeeApplication)
