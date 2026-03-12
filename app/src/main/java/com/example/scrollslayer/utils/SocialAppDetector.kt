package com.example.scrollslayer.utils

import com.example.scrollslayer.data.model.SocialAppInfo

object SocialAppsDetector {

    private val supportedApps = listOf(
        SocialAppInfo(
            packageName = "com.zhiliaoapp.musically",
            displayName = "TikTok",
            icon = "🎵"
        ),
        SocialAppInfo(
            packageName = "com.instagram.android",
            displayName = "Instagram",
            icon = "📸"
        ),
        SocialAppInfo(
            packageName = "com.google.android.youtube",
            displayName = "YouTube",
            icon = "▶️"
        ),
        SocialAppInfo(
            packageName = "com.twitter.android",
            displayName = "X / Twitter",
            icon = "🐦"
        ),
        SocialAppInfo(
            packageName = "com.facebook.katana",
            displayName = "Facebook",
            icon = "📘"
        ),
        SocialAppInfo(
            packageName = "com.reddit.frontpage",
            displayName = "Reddit",
            icon = "👽"
        ),
        SocialAppInfo(
            packageName = "org.telegram.messenger",
            displayName = "Telegram",
            icon = "✈️"
        ),
        SocialAppInfo(
            packageName = "com.discord",
            displayName = "Discord",
            icon = "🎮"
        ),
        SocialAppInfo(
            packageName = "com.snapchat.android",
            displayName = "Snapchat",
            icon = "👻"
        ),
        SocialAppInfo(
            packageName = "com.pinterest",
            displayName = "Pinterest",
            icon = "📌"
        )
    )

    fun getSocialApp(packageName: String): SocialAppInfo? {
        return supportedApps.firstOrNull { it.packageName == packageName }
    }

    fun isSocialApp(packageName: String): Boolean {
        return supportedApps.any { it.packageName == packageName }
    }
}