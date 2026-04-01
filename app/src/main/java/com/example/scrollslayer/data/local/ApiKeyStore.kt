package com.example.scrollslayer.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Securely stores user API keys using AES-256 encryption.
 * Keys are encrypted at rest via Android Keystore-backed
 * EncryptedSharedPreferences.
 */
class ApiKeyStore(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "learnpath_secure_keys",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveKey(provider: String, apiKey: String) {
        prefs.edit().putString("key_$provider", apiKey).apply()
    }

    fun getKey(provider: String): String? {
        return prefs.getString("key_$provider", null)
    }

    fun removeKey(provider: String) {
        prefs.edit().remove("key_$provider").apply()
    }

    fun hasKey(provider: String): Boolean {
        return !getKey(provider).isNullOrBlank()
    }

    fun getActiveProvider(): String? {
        return prefs.getString("active_provider", null)
    }

    fun setActiveProvider(provider: String?) {
        if (provider == null) {
            prefs.edit().remove("active_provider").apply()
        } else {
            prefs.edit().putString("active_provider", provider).apply()
        }
    }

    fun getConfiguredProviders(): Set<String> {
        return listOf("claude", "openai", "gemini", "groq")
            .filter { hasKey(it) }
            .toSet()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
