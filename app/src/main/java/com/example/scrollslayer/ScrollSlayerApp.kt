package com.example.scrollslayer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scrollslayer.data.local.entity.MissionEntity
import com.example.scrollslayer.ui.screens.DashboardScreen
import com.example.scrollslayer.ui.screens.MissionDetailScreen
import com.example.scrollslayer.ui.screens.MissionsScreen
import com.example.scrollslayer.ui.theme.BgColor
import com.example.scrollslayer.ui.theme.Gold
import com.example.scrollslayer.ui.theme.GoldSoft
import com.example.scrollslayer.ui.theme.PanelColor
import com.example.scrollslayer.ui.theme.TextSecondary
import com.example.scrollslayer.viewmodel.DashboardViewModel

sealed class AppScreen {
    data object Dashboard : AppScreen()
    data object Missions : AppScreen()
    data class MissionDetail(val mission: MissionEntity) : AppScreen()
}

private fun AppScreen.isRootScreen() =
    this is AppScreen.Dashboard || this is AppScreen.Missions

@Composable
fun ScrollSlayerApp() {
    val dashboardViewModel: DashboardViewModel = viewModel()
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Dashboard) }

    Scaffold(
        containerColor = BgColor,
        bottomBar = {
            if (currentScreen.isRootScreen()) {
                ScrollSlayerBottomNav(
                    currentScreen = currentScreen,
                    onNavigate = { currentScreen = it }
                )
            }
        }
    ) { innerPadding ->
        when (val screen = currentScreen) {
            AppScreen.Dashboard -> {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    contentPadding = innerPadding,
                    onOpenMissions = { currentScreen = AppScreen.Missions }
                )
            }
            AppScreen.Missions -> {
                MissionsScreen(
                    contentPadding = innerPadding,
                    onMissionSelected = { mission ->
                        currentScreen = AppScreen.MissionDetail(mission)
                    }
                )
            }
            is AppScreen.MissionDetail -> {
                MissionDetailScreen(
                    mission = screen.mission,
                    onBack = { currentScreen = AppScreen.Missions }
                )
            }
        }
    }
}

@Composable
private fun ScrollSlayerBottomNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = PanelColor,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = currentScreen is AppScreen.Dashboard,
            onClick = { onNavigate(AppScreen.Dashboard) },
            icon = { Text("\u2694", fontSize = 20.sp) },
            label = {
                Text(
                    text = "Dashboard",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Gold,
                selectedTextColor = Gold,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Gold.copy(alpha = 0.12f)
            )
        )
        NavigationBarItem(
            selected = currentScreen is AppScreen.Missions,
            onClick = { onNavigate(AppScreen.Missions) },
            icon = { Text("\u2691", fontSize = 20.sp) },
            label = {
                Text(
                    text = "Misiones",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Gold,
                selectedTextColor = Gold,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Gold.copy(alpha = 0.12f)
            )
        )
    }
}
