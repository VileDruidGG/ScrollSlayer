package com.example.scrollslayer.data.repository

import com.example.scrollslayer.data.model.Goal
import com.example.scrollslayer.data.model.SocialUsage

class GoalRepository {

    fun getGoal(): Goal? {
        return Goal(
            id = "1",
            name = "Aprender francés",
            createdAt = System.currentTimeMillis()
        )
    }

    fun getSocialUsage(): List<SocialUsage> {
        return listOf(
            SocialUsage(
                appName = "TikTok",
                minutes = 22,
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