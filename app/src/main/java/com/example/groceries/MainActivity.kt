package com.example.groceries

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.groceries.ui.navigation.AppNavGraph
import com.example.groceries.ui.theme.GroceriesTheme
import androidx.work.*
import com.example.groceries.ui.notifications.createNotificationChannel
import com.example.groceries.ui.notifications.scheduleExpiryChecks
import com.example.groceries.ui.workers.ExpiryNotificationWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {

        val workRequest =
            PeriodicWorkRequestBuilder<ExpiryNotificationWorker>(
                1,TimeUnit.DAYS
            ).build()

        createNotificationChannel(this)
        scheduleExpiryChecks(this)

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "expiry_notifications",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
        super.onCreate(savedInstanceState)
        setContent {
            AppNavGraph()
        }
    }
}