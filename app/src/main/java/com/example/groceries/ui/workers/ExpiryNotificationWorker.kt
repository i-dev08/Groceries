package com.example.groceries.ui.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.groceries.ui.data.DatabaseProvider
import com.example.groceries.ui.data.NotificationEntity
import java.util.concurrent.TimeUnit
import com.example.groceries.R

class ExpiryNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context,params) {

    override suspend fun doWork(): Result {
        val db = DatabaseProvider.getDatabase(applicationContext)
        val groceryDao = db.groceryDao()
        val notificationDao = db.notificationDao()

        val now = System.currentTimeMillis()
        val twoDaysMillis = TimeUnit.DAYS.toMillis(2)

        val expiringItems = groceryDao.getItemsExpiringBefore(
            now + twoDaysMillis
        )

        if (expiringItems.isEmpty()) return Result.success()

        createNotificationChannel()

        expiringItems.forEach { item ->
            val title = "Item expiring soon"
            val message = "${item.name} expires in 2 days"

            notificationDao.insert(
                NotificationEntity(
                    title = title,
                    message = message,
                    isRead = false,
                    timestamp = System.currentTimeMillis()
                )
            )

            val notification = NotificationCompat.Builder(
                applicationContext,
                "expiry_channel"
            )
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            val manager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            manager.notify(item.id.toInt(), notification)
        }
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "expiry_channel",
                "Expiry Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}