package com.example.scrollslayer.data.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import com.example.scrollslayer.data.model.SocialUsage
import java.util.Calendar
class UsageRepository(
    private val context: Context
) {

    private val socialApps = mapOf(
        "com.zhiliaoapp.musically" to "TikTok",
        "com.instagram.android" to "Instagram",
        "com.google.android.youtube" to "YouTube",
        "com.twitter.android" to "Twitter",
        "com.facebook.katana" to "Facebook"
    )
    fun getSocialUsage(): List<SocialUsage> {

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val calendar = Calendar.getInstance()

        val endTime = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val startTime = calendar.timeInMillis

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        val result = mutableListOf<SocialUsage>()

        stats?.forEach { usage ->
            val packageName = usage.packageName

            if (socialApps.containsKey(packageName)) {
                val minutes = (usage.totalTimeInForeground / 1000 / 60).toInt()

                if (minutes > 0) {
                    result.add(
                        SocialUsage(
                            appName = socialApps[packageName]!!,
                            minutes = minutes,
                            icon = "📱"
                        )
                    )
                }
            }
        }
        return result
    }
}