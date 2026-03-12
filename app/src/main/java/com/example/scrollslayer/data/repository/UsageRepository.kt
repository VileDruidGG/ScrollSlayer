package com.example.scrollslayer.data.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import com.example.scrollslayer.data.model.SocialUsage
import com.example.scrollslayer.utils.SocialAppsDetector
import java.util.Calendar
class UsageRepository(
    private val context: Context
) {

    fun getSocialUsage(): List<SocialUsage> {

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val calendar = Calendar.getInstance()

        val endTime = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startTime = calendar.timeInMillis

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        if (stats.isNullOrEmpty()) return emptyList()


        return stats.mapNotNull { usage ->
            val socialApp = SocialAppsDetector.getSocialApp(usage.packageName)
                ?: return@mapNotNull null

            val minutes = (usage.totalTimeInForeground / 1000 / 60).toInt()

            if (minutes <= 0) return@mapNotNull null

            SocialUsage(
                packageName = socialApp.packageName,
                appName = socialApp.displayName,
                minutes = minutes,
                icon = socialApp.icon
            )
        }.sortedByDescending { it.minutes }
    }
}