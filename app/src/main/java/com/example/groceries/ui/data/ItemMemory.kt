package com.example.groceries.ui.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//stores the data of the item but it can only be edited no deleted
@Entity(tableName = "item_memory")
data class ItemMemory(
    @PrimaryKey
    val name: String,

    val category: String,
    val defaultUnit: String
)
