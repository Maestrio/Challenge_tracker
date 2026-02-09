package com.example.challengetracker.model

import java.time.LocalDate

data class ChallengeOverview(
    val id: Long,
    val name: String,
    val day: Int,
    val totalDays: Int,
    val currentPercent: Int,
    val progressFraction: Float,
    val riskStatus: RiskStatus,
    val terminalGoal: String,
    val postChallengeRule: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val successThreshold: Int
)
