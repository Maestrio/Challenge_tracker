package com.example.challengetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "check_ins")
data class CheckInEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val challengeId: Long,
    val date: String,
    val didSucceed: Boolean,
    val note: String?
)
