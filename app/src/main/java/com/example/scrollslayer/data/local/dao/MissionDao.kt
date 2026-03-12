package com.example.scrollslayer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.scrollslayer.data.local.entity.MissionEntity

@Dao
interface MissionDao {

    @Insert
    suspend fun insertMission(mission: MissionEntity): Long

    @Update
    suspend fun updateMission(mission: MissionEntity)

    @Query("SELECT * FROM missions ORDER BY createdAt DESC")
    suspend fun getAllMissions(): List<MissionEntity>

    @Query("SELECT * FROM missions WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveMission(): MissionEntity?

    @Query("UPDATE missions SET isActive = 0")
    suspend fun clearActiveMission()

    @Query("DELETE FROM missions WHERE id = :missionId")
    suspend fun deleteMissionById(missionId: Long)
}