package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val targetDate: Long = 0L,
    val isCompleted: Boolean = false,
    val progress: Float = 0f,
    val taskCount: Int = 0,
    val streak: Int = 0,
    val isSynced: Boolean = false
)
