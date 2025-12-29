package com.example.groceries.ui.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//stores the data of the item and can be deleted
@Entity(tableName = "grocery_items")
data class GroceryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val category: String,
    val expiry: Long,

    val quantityValue: Int,
    val quantityUnit: String
)
