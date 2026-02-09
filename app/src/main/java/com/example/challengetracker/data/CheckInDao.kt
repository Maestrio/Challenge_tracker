package com.example.challengetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CheckInDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkIn: CheckInEntity)

    @Query("DELETE FROM check_ins WHERE challengeId = :challengeId")
    suspend fun deleteForChallenge(challengeId: Long)
}
