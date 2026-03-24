package com.example.scrollslayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scrollslayer.data.local.entity.MissionEntity
import com.example.scrollslayer.data.local.database.DatabaseProvider
import com.example.scrollslayer.data.repository.MissionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MissionUiState(
    val missions: List<MissionEntity> = emptyList(),
    val activeMission: MissionEntity? = null
)

class MissionViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = DatabaseProvider.getDatabase(application)
    private val missionRepository = MissionRepository(database.missionDao())

    private val _uiState = MutableStateFlow(MissionUiState())
    val uiState: StateFlow<MissionUiState> = _uiState.asStateFlow()

    init {
        loadMissions()
    }

    fun loadMissions() {
        viewModelScope.launch {
            val missions = missionRepository.getAllMissions()
            val activeMission = missionRepository.getActiveMission()

            _uiState.value = MissionUiState(
                missions = missions,
                activeMission = activeMission
            )
        }
    }

    fun createMission(
        title: String,
        description: String,
        isActive: Boolean = false
    ) {
        viewModelScope.launch {
            missionRepository.createMission(
                title = title,
                description = description,
                isActive = isActive
            )
            loadMissions()
        }
    }

    fun setActiveMission(mission: MissionEntity) {
        viewModelScope.launch {
            missionRepository.setActiveMission(mission)
            loadMissions()
        }
    }

    fun deleteMission(missionId: Long) {
        viewModelScope.launch {
            missionRepository.deleteMission(missionId)
            loadMissions()
        }
    }
}