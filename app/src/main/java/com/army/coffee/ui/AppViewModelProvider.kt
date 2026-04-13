package com.army.coffee.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.army.coffee.ArmyCoffeeApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            CoffeeViewModel(
                armyCoffeeApplication().container.coffeeRepository
            )
        }
    }
}

fun CreationExtras.armyCoffeeApplication(): ArmyCoffeeApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ArmyCoffeeApplication)
