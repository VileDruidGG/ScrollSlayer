package com.example.scrollslayer.viewmodel

import androidx.lifecycle.ViewModel
import com.example.scrollslayer.data.model.SocialUsage
import com.example.scrollslayer.data.repository.GoalRepository
import com.example.scrollslayer.data.repository.UsageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardUiState(
    val goalName: String = "Aprender francés",
    val socialApps: List<SocialUsage> = emptyList(),
)

class DashboardViewModel : ViewModel() {

    private val goalRepository = GoalRepository()
    private val usageRepository = UsageRepository()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        val goal = goalRepository.getGoal()
        val socialApps = usageRepository.getSocialUsage()


        _uiState.value = DashboardUiState(
            goalName = goal?.name ?: "Sin meta definida",
            socialApps = socialApps
        )
    }
}