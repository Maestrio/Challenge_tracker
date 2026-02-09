package com.example.challengetracker.model

import com.example.challengetracker.data.CheckInEntity
import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class RiskStatus {
    ON_TRACK,
    AT_RISK,
    BELOW_THRESHOLD
}

data class ChallengeProgress(
    val totalDays: Int,
    val completedDays: Int,
    val missedDays: Int,
    val currentPercent: Int,
    val streak: Int,
    val riskStatus: RiskStatus
)

fun calculateProgress(
    startDate: LocalDate,
    durationDays: Int,
    successThreshold: Int,
    checkIns: List<CheckInEntity>
): ChallengeProgress {
    val today = LocalDate.now()
    val endDate = startDate.plusDays(durationDays.toLong() - 1)
    val daysElapsed = when {
        today.isBefore(startDate) -> 0
        today.isAfter(endDate) -> durationDays
        else -> ChronoUnit.DAYS.between(startDate, today).toInt() + 1
    }
    val checkInMap = checkIns.associateBy { LocalDate.parse(it.date) }
    var completed = 0
    var missed = 0
    var currentStreak = 0

    for (dayOffset in 0 until daysElapsed) {
        val date = startDate.plusDays(dayOffset.toLong())
        val entry = checkInMap[date]
        if (entry?.didSucceed == true) {
            completed += 1
            currentStreak += 1
        } else if (entry?.didSucceed == false) {
            missed += 1
            currentStreak = 0
        }
    }

    val currentPercent = if (daysElapsed == 0) 100 else (completed * 100 / daysElapsed)
    val projectedPercent = if (daysElapsed == 0) 100 else ((completed) * 100 / daysElapsed)
    val riskStatus = when {
        projectedPercent >= successThreshold -> RiskStatus.ON_TRACK
        projectedPercent >= successThreshold - 10 -> RiskStatus.AT_RISK
        else -> RiskStatus.BELOW_THRESHOLD
    }

    return ChallengeProgress(
        totalDays = durationDays,
        completedDays = completed,
        missedDays = missed,
        currentPercent = currentPercent,
        streak = currentStreak,
        riskStatus = riskStatus
    )
}
