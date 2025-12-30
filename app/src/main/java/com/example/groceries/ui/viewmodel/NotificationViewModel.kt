package com.example.groceries.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope

import com.example.groceries.ui.data.NotificationEntity
import com.example.groceries.ui.data.NotificationDao

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotificationViewModel (
    private val dao: NotificationDao
) : ViewModel() {

    val notifications: LiveData<List<NotificationEntity>> = dao.getAllNotifications()

    fun markAllAsRead() {
        viewModelScope.launch {
            dao.markAllAsRead()
        }
    }
}