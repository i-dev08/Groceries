package com.example.groceries.ui.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.groceries.ui.data.GroceryItem


@Database(
    entities = [
        GroceryItem::class,
        ItemMemory::class,
        NotificationEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class GroceryDatabase : RoomDatabase() {
    abstract fun groceryDao(): GroceryDao

    abstract fun notificationDao(): NotificationDao
}