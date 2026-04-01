package com.example.scrollslayer.workers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkScheduler {

    private const val USAGE_MONITOR_WORK_NAME = "usage_monitor_work"

    fun scheduleUsageMonitor(context: Context) {
        val request = PeriodicWorkRequestBuilder<UsageMonitorWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            USAGE_MONITOR_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}