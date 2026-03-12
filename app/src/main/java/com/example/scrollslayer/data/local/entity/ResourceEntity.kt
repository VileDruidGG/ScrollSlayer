package com.example.scrollslayer.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "resources",
    foreignKeys = [
        ForeignKey(
            entity = MissionEntity::class,
            parentColumns = ["id"],
            childColumns = ["missionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["missionId"])]
)
data class ResourceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val missionId: Long,
    val title: String,
    val url: String,
    val type: String,
    val createdAt: Long = System.currentTimeMillis()
)