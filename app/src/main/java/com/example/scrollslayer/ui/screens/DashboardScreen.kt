package com.example.scrollslayer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scrollslayer.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { innerPadding ->
        DashboardContent(
            paddingValues = innerPadding,
            socialTimeMinutes = uiState.socialTimeMinutes,
            goalName = uiState.goalName,
            onRefresh = viewModel::loadDashboard
        )
    }
}

@Composable
private fun DashboardContent(
    paddingValues: PaddingValues,
    socialTimeMinutes: Int,
    goalName: String,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "ScrollSlayer",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Tiempo en redes hoy: $socialTimeMinutes min",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Meta actual: $goalName",
            style = MaterialTheme.typography.bodyLarge
        )

        Button(onClick = onRefresh) {
            Text(text = "Recargar")
        }
    }
}