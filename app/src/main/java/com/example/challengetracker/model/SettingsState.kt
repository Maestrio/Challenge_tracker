package com.example.challengetracker.model

import java.time.DayOfWeek
import java.time.LocalTime

data class SettingsState(
    val dailyReminderEnabled: Boolean = false,
    val dailyReminderTime: LocalTime = LocalTime.of(20, 0),
    val weeklyReviewEnabled: Boolean = false,
    val weeklyReviewDay: DayOfWeek = DayOfWeek.SUNDAY,
    val defaultSuccessThreshold: Int = 90,
    val darkModeEnabled: Boolean = false
)
