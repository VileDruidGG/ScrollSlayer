package com.example.scrollslayer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scrollslayer.ui.screens.DashboardScreen
import com.example.scrollslayer.viewmodel.DashboardViewModel

@Composable
fun ScrollSlayerApp() {
    val viewModel: DashboardViewModel = viewModel()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        DashboardScreen(viewModel = viewModel)
    }
}