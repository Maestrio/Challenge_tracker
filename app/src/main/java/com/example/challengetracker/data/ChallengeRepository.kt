package com.example.challengetracker.data

import com.example.challengetracker.model.ChallengeDetail
import com.example.challengetracker.model.ChallengeOverview
import com.example.challengetracker.model.calculateProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ChallengeRepository(
    private val challengeDao: ChallengeDao,
    private val checkInDao: CheckInDao
) {
    fun observeActiveChallenges(): Flow<List<ChallengeOverview>> {
        return challengeDao.observeActiveChallenges().map { challenges ->
            challenges.map { it.toOverview() }
        }
    }

    fun observeArchivedChallenges(): Flow<List<ChallengeOverview>> {
        return challengeDao.observeArchivedChallenges().map { challenges ->
            challenges.map { it.toOverview() }
        }
    }

    fun observeChallenge(id: Long): Flow<ChallengeOverview?> {
        return challengeDao.observeChallenge(id).map { it?.toOverview() }
    }

    fun observeChallengeDetail(id: Long): Flow<ChallengeDetail?> {
        return challengeDao.observeChallenge(id).map { detail ->
            detail?.let { ChallengeDetail(it.toOverview(), it.checkIns) }
        }
    }

    suspend fun createChallenge(challenge: ChallengeEntity): Long = challengeDao.upsert(challenge)

    suspend fun archiveChallenge(id: Long) = challengeDao.setArchived(id, true)

    suspend fun deleteChallenge(id: Long) {
        checkInDao.deleteForChallenge(id)
        challengeDao.delete(id)
    }

    suspend fun addCheckIn(checkIn: CheckInEntity) = checkInDao.insert(checkIn)
}

private fun ChallengeWithCheckIns.toOverview(): ChallengeOverview {
    val startDate = LocalDate.parse(challenge.startDate)
    val progress = calculateProgress(
        startDate = startDate,
        durationDays = challenge.durationDays,
        successThreshold = challenge.successThreshold,
        checkIns = checkIns
    )
    val today = LocalDate.now()
    val dayCount = when {
        today.isBefore(startDate) -> 0
        today.isAfter(startDate.plusDays(challenge.durationDays.toLong() - 1)) -> challenge.durationDays
        else -> ChronoUnit.DAYS.between(startDate, today).toInt() + 1
    }
    return ChallengeOverview(
        id = challenge.id,
        name = challenge.name,
        day = dayCount,
        totalDays = challenge.durationDays,
        currentPercent = progress.currentPercent,
        progressFraction = if (dayCount == 0) 0f else dayCount.toFloat() / challenge.durationDays.toFloat(),
        riskStatus = progress.riskStatus,
        terminalGoal = challenge.terminalGoal.orEmpty(),
        postChallengeRule = challenge.postChallengeRule.orEmpty(),
        startDate = startDate,
        endDate = startDate.plusDays(challenge.durationDays.toLong() - 1),
        successThreshold = challenge.successThreshold
    )
}
