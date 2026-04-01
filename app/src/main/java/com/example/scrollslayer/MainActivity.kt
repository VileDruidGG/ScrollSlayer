package com.example.scrollslayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.scrollslayer.data.remote.RetrofitClient
import com.example.scrollslayer.ui.theme.ScrollSlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize networking for LearnPath
        RetrofitClient.initialize(this)

        setContent {
            ScrollSlayerTheme {
                ScrollSlayerApp()
            }
        }
    }
}
