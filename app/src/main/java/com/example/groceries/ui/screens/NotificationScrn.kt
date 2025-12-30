package com.example.groceries.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.ui.platform.LocalContext

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.livedata.observeAsState

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.groceries.ui.data.DatabaseProvider
import com.example.groceries.ui.data.NotificationEntity

import com.example.groceries.ui.viewmodel.NotificationViewModel
import com.example.groceries.ui.viewmodel.NotificationViewModelFactory

@Composable
fun NotificationsScrn() {

    val context = LocalContext.current
    val dao = remember {
        DatabaseProvider.getDatabase(context).notificationDao()
    }

    val viewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(dao)
    )

    val notifications by viewModel.notifications.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.markAllAsRead()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Notifications",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        if(notifications.isEmpty()) {
            EmptyNotificationsState()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(notifications) { notification ->
                    NotificationItem(notification)
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationEntity) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFF4CAF50), CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.width(18.dp))
        }

        Column {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyNotificationsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "You have no notifications",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
