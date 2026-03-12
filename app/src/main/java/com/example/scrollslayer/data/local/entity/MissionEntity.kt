package com.example.scrollslayer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)