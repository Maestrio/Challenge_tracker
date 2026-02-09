package com.example.challengetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(challenge: ChallengeEntity): Long

    @Query("UPDATE challenges SET isArchived = :archived WHERE id = :id")
    suspend fun setArchived(id: Long, archived: Boolean)

    @Query("DELETE FROM challenges WHERE id = :id")
    suspend fun delete(id: Long)

    @Transaction
    @Query("SELECT * FROM challenges WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun observeActiveChallenges(): Flow<List<ChallengeWithCheckIns>>

    @Transaction
    @Query("SELECT * FROM challenges WHERE isArchived = 0 ORDER BY createdAt DESC")
    suspend fun getActiveChallenges(): List<ChallengeWithCheckIns>

    @Transaction
    @Query("SELECT * FROM challenges WHERE isArchived = 1 ORDER BY createdAt DESC")
    fun observeArchivedChallenges(): Flow<List<ChallengeWithCheckIns>>

    @Transaction
    @Query("SELECT * FROM challenges WHERE id = :id")
    fun observeChallenge(id: Long): Flow<ChallengeWithCheckIns?>
}
