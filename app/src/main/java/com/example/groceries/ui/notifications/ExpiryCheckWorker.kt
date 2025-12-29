package com.example.groceries.ui.notifications

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.groceries.R
import com.example.groceries.ui.data.DatabaseProvider
import com.example.groceries.ui.data.NotificationEntity
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking

const val CHANNEL_ID = "expiry_alerts"

class ExpiryCheckWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {
        val db = DatabaseProvider
            .getDatabase(applicationContext)

        val groceryDao = db.groceryDao()
        val notificationDao = db.notificationDao()

        val items = groceryDao.getAllItemsSync()
        val now = System.currentTimeMillis()

        items.forEach { item ->
            val daysLeft =
                TimeUnit.MILLISECONDS.toDays(item.expiry - now)

            //checks for the days left till the expiry and checks for notifications
            if (daysLeft in 0..2) {
                sendNotification(
                    name =item.name,
                    daysLeft = daysLeft,
                    notificationDao = notificationDao
                )
            }
        }

        return Result.success()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
     private fun sendNotification(
        name: String,
        daysLeft: Long,
        notificationDao: com.example.groceries.ui.data.NotificationDao
     ) {

        val text =
            if (daysLeft == 0L) "$name expires today"
            else "$name expires in $daysLeft day(s)"

        //a new entry to the notifications table
        runBlocking {
            notificationDao.insert(
                NotificationEntity(
                    title = "Expiry Alert",
                    message = text,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Expiry Alert")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat
            .from(applicationContext)
            .notify(name.hashCode(), notification)
    }
}