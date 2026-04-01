package com.example.scrollslayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.scrollslayer.notifications.NotificationHelper
import com.example.scrollslayer.ui.theme.ScrollSlayerTheme
import com.example.scrollslayer.workers.WorkScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannel(this)
        WorkScheduler.scheduleUsageMonitor(this)

        setContent {
            ScrollSlayerTheme {
                ScrollSlayerApp()
            }
        }
    }
}