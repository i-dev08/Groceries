package com.example.groceries.ui.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun scheduleExpiryChecks(context: Context) {

    val workRequest =
        PeriodicWorkRequestBuilder<ExpiryCheckWorker>(
            12, TimeUnit.HOURS
        ).build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "expiry_check",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
}