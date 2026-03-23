package com.example.scrollslayer.data.repository

import com.example.scrollslayer.data.local.dao.ResourceDao
import com.example.scrollslayer.data.local.entity.ResourceEntity

class ResourceRepository(
    private val resourceDao: ResourceDao
) {

    suspend fun addResource(
        missionId: Long,
        title: String,
        url: String,
        type: String
    ): Long {
        return resourceDao.insertResource(
            ResourceEntity(
                missionId = missionId,
                title = title,
                url = url,
                type = type
            )
        )
    }

    suspend fun getResourcesByMission(
        missionId: Long
    ): List<ResourceEntity> {
        return resourceDao.getResourcesByMissionId(missionId)
    }

    suspend fun deleteResource(resourceId: Long) {
        resourceDao.deleteResourceById(resourceId)
    }

    suspend fun updateResource(resource: ResourceEntity) {
        resourceDao.updateResource(resource)
    }
}