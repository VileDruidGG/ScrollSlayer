package com.example.scrollslayer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scrollslayer.data.local.entity.MissionEntity
import com.example.scrollslayer.ui.screens.DashboardScreen
import com.example.scrollslayer.ui.screens.MissionDetailScreen
import com.example.scrollslayer.ui.screens.MissionsScreen
import com.example.scrollslayer.viewmodel.DashboardViewModel

sealed class AppScreen {
    data object Dashboard : AppScreen()
    data object Missions : AppScreen()
    data class MissionDetail(val mission: MissionEntity) : AppScreen()
}

@Composable
fun ScrollSlayerApp() {
    val dashboardViewModel: DashboardViewModel = viewModel()

    var currentScreen by remember {
        mutableStateOf<AppScreen>(AppScreen.Dashboard)
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        when (val screen = currentScreen) {
            AppScreen.Dashboard -> {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onOpenMissions = {
                        currentScreen = AppScreen.Missions
                    }
                )
            }

            AppScreen.Missions -> {
                MissionsScreen(
                    onBack = {
                        currentScreen = AppScreen.Dashboard
                    },
                    onMissionSelected = { mission ->
                        currentScreen = AppScreen.MissionDetail(mission)
                    }
                )
            }

            is AppScreen.MissionDetail -> {
                MissionDetailScreen(
                    mission = screen.mission,
                    onBack = {
                        currentScreen = AppScreen.Missions
                    }
                )
            }
        }
    }
}