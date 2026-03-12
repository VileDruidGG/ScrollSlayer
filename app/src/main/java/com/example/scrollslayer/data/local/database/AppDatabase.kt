package com.example.scrollslayer.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.scrollslayer.data.local.dao.MissionDao
import com.example.scrollslayer.data.local.dao.ResourceDao
import com.example.scrollslayer.data.local.entity.MissionEntity
import com.example.scrollslayer.data.local.entity.ResourceEntity

@Database(
    entities = [MissionEntity::class, ResourceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun missionDao(): MissionDao
    abstract fun resourceDao(): ResourceDao
}