package com.example.scrollslayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scrollslayer.data.local.database.DatabaseProvider
import com.example.scrollslayer.data.local.entity.ResourceEntity
import com.example.scrollslayer.data.repository.ResourceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ResourceUiState(
    val missionId: Long = 0,
    val resources: List<ResourceEntity> = emptyList()
)

class ResourceViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = DatabaseProvider.getDatabase(application)
    private val resourceRepository = ResourceRepository(database.resourceDao())

    private val _uiState = MutableStateFlow(ResourceUiState())
    val uiState: StateFlow<ResourceUiState> = _uiState.asStateFlow()

    fun loadResources(missionId: Long) {
        viewModelScope.launch {
            val resources = resourceRepository.getResourcesByMission(missionId)

            _uiState.value = ResourceUiState(
                missionId = missionId,
                resources = resources
            )
        }
    }

    fun addResource(
        missionId: Long,
        title: String,
        url: String,
        type: String
    ) {
        viewModelScope.launch {
            resourceRepository.addResource(
                missionId = missionId,
                title = title,
                url = url,
                type = type
            )
            loadResources(missionId)
        }
    }

    fun deleteResource(
        missionId: Long,
        resourceId: Long
    ) {
        viewModelScope.launch {
            resourceRepository.deleteResource(resourceId)
            loadResources(missionId)
        }
    }
}