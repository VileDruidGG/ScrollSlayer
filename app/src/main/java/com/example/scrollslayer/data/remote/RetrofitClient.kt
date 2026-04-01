package com.example.scrollslayer.data.remote

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit client for the LearnPath backend.
 *
 * Call RetrofitClient.initialize(context) once from your Activity
 * before using the api property.
 *
 * BASE_URL setup:
 *   - Emulator  → "http://10.0.2.2:8080/"   (alias for host machine localhost)
 *   - Physical  → "http://<PC_LOCAL_IP>:8080/" (e.g. "http://192.168.20.42:8080/")
 *   - Production → "https://your-domain.com/"
 *
 * To find your PC's local IP:
 *   Windows:  ipconfig → look for IPv4 under Wi-Fi
 *   Mac/Linux: ifconfig | grep "inet " or ip addr
 *
 * IMPORTANT: Your phone and PC must be on the SAME Wi-Fi network,
 * and the backend must listen on 0.0.0.0 (not just 127.0.0.1).
 */
object RetrofitClient {

    // ★ CHANGE THIS to your PC's local IP when testing on a physical device
    // Example: "http://192.168.20.42:8080/"
    // Use "http://10.0.2.2:8080/" for emulator only
    private const val BASE_URL = "http://192.168.20.211:8080/"

    private var deviceId: String = UUID.randomUUID().toString()
    private var _api: LearnPathApi? = null

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences("learnpath_device", Context.MODE_PRIVATE)
        deviceId = prefs.getString("device_id", null) ?: run {
            val newId = UUID.randomUUID().toString()
            prefs.edit().putString("device_id", newId).apply()
            newId
        }

        val deviceInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("X-Device-Id", deviceId)
                .build()
            chain.proceed(request)
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(deviceInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        _api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LearnPathApi::class.java)
    }

    val api: LearnPathApi
        get() = _api ?: throw IllegalStateException(
            "RetrofitClient not initialized. Call RetrofitClient.initialize(context) first."
        )
}
