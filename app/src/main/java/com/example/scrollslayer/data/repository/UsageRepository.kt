package com.example.scrollslayer.data.repository
import com.example.scrollslayer.data.model.SocialUsage



class UsageRepository {
    fun getSocialUsage(): List<SocialUsage> {
        return listOf(
            SocialUsage(
                appName = "TikTok",
                minutes = 25,
                icon = "🎵"
            ),
            SocialUsage(
                appName = "Instagram",
                minutes = 15,
                icon = "📸"
            ),
            SocialUsage(
                appName = "YouTube",
                minutes = 10,
                icon = "▶️"
            )
        )
    }

}