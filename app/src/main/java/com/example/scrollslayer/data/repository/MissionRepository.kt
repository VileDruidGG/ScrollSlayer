package com.example.scrollslayer.data.repository

import com.example.scrollslayer.data.local.dao.MissionDao
import com.example.scrollslayer.data.local.entity.MissionEntity

class MissionRepository(
    private val missionDao: MissionDao
) {

    suspend fun createMission(
        title: String,
        description: String,
        isActive: Boolean = false
    ): Long {
        if (isActive) {
            missionDao.clearActiveMission()
        }

        return missionDao.insertMission(
            MissionEntity(
                title = title,
                description = description,
                isActive = isActive
            )
        )
    }

    suspend fun getAllMissions(): List<MissionEntity> {
        return missionDao.getAllMissions()
    }

    suspend fun getActiveMission(): MissionEntity? {
        return missionDao.getActiveMission()
    }

    suspend fun setActiveMission(mission: MissionEntity) {
        missionDao.clearActiveMission()
        missionDao.updateMission(
            mission.copy(isActive = true)
        )
    }

    suspend fun deleteMission(missionId: Long) {
        missionDao.deleteMissionById(missionId)
    }

    suspend fun updateMission(mission: MissionEntity) {
        missionDao.updateMission(mission)
    }
}