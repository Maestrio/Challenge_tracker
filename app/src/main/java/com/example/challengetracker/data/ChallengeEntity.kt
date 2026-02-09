package com.example.challengetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.challengetracker.model.ChallengeType

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val description: String?,
    val durationDays: Int,
    val startDate: String,
    val successThreshold: Int,
    val type: ChallengeType,
    val terminalGoal: String?,
    val postChallengeRule: String?,
    val isArchived: Boolean = false,
    val createdAt: String
)
