package com.example.scrollslayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.scrollslayer.data.model.SocialUsage
import com.example.scrollslayer.data.repository.GoalRepository
import com.example.scrollslayer.data.repository.UsageRepository
import com.example.scrollslayer.utils.UsagePermissionChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardUiState(
    val goalName: String = "Aprender francés",
    val socialApps: List<SocialUsage> = emptyList(),
    val totalMinutes: Int = 0,
    val hasUsagePermission: Boolean = false
)

class DashboardViewModel(
    application: Application
) : AndroidViewModel(application){

    private val goalRepository = GoalRepository()
    private val usageRepository = UsageRepository(application)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        val app = getApplication<Application>()
        val hasPermission = UsagePermissionChecker.hasUsageStatsPermission(app)
        val goal = goalRepository.getGoal()

        val socialApps = if (hasPermission) {
            usageRepository.getSocialUsage()
        } else {
            emptyList()
        }

        val totalMinutes = socialApps.sumOf { it.minutes }

        _uiState.value = DashboardUiState(
            goalName = goal?.name ?: "Sin meta definida",
            socialApps = socialApps,
            totalMinutes = totalMinutes,
            hasUsagePermission = hasPermission
        )
    }
}