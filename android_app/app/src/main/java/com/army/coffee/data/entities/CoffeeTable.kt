package com.army.coffee.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coffee_tables")
data class CoffeeTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val isOccupied: Boolean = false
)
