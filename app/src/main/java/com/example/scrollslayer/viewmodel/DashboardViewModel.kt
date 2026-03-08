package com.example.scrollslayer.viewmodel

import androidx.lifecycle.ViewModel
import com.example.scrollslayer.data.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardUiState(
    val socialTimeMinutes: Int = 0,
    val goalName: String = "Sin meta definida"
)

class DashboardViewModel : ViewModel() {

    private val goalRepository = GoalRepository()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        val goal = goalRepository.getGoal()

        _uiState.value = DashboardUiState(
            socialTimeMinutes = 0,
            goalName = goal?.name ?: "Sin meta definida"
        )
    }
}