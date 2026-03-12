package com.example.scrollslayer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.scrollslayer.data.local.entity.ResourceEntity

@Dao
interface ResourceDao {

    @Insert
    suspend fun insertResource(resource: ResourceEntity): Long

    @Update
    suspend fun updateResource(resource: ResourceEntity)

    @Query("SELECT * FROM resources WHERE missionId = :missionId ORDER BY createdAt DESC")
    suspend fun getResourcesByMissionId(missionId: Long): List<ResourceEntity>

    @Query("DELETE FROM resources WHERE id = :resourceId")
    suspend fun deleteResourceById(resourceId: Long)
}