package com.example.scrollslayer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scrollslayer.data.local.entity.MissionEntity
import com.example.scrollslayer.ui.screens.DashboardScreen
import com.example.scrollslayer.ui.screens.MissionDetailScreen
import com.example.scrollslayer.ui.screens.MissionsScreen
import com.example.scrollslayer.viewmodel.DashboardViewModel

@Composable
fun ScrollSlayerApp() {
    val viewModel: DashboardViewModel = viewModel()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        //DashboardScreen(viewModel = viewModel)
       // MissionsScreen()
        MissionDetailScreen(
            mission = MissionEntity(
                id = 1,
                title = "Aprender francés",
                description = "Practicar listening y vocabulario",
                isActive = true
            )
        )
    }
}