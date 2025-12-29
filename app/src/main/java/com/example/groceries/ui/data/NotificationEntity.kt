package com.example.groceries.ui.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//stores the notificaition
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
