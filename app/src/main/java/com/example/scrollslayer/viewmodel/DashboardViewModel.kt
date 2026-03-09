package com.example.scrollslayer.viewmodel

import android.content.Context
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
    val totalMinutes: Int = 0
)

class DashboardViewModel(
    private val context: Context
) : ViewModel() {

    private val goalRepository = GoalRepository()
    private val usageRepository = UsageRepository(context)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        val goal = goalRepository.getGoal()
        val socialApps = usageRepository.getSocialUsage()
        val totalMinutes = socialApps.sumOf { it.minutes }


        _uiState.value = DashboardUiState(
            goalName = goal?.name ?: "Sin meta definida",
            socialApps = socialApps,
            totalMinutes = totalMinutes
        )
    }
}