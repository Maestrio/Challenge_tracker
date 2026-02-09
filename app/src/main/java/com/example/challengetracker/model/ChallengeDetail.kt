package com.example.challengetracker.model

import com.example.challengetracker.data.CheckInEntity

data class ChallengeDetail(
    val overview: ChallengeOverview,
    val checkIns: List<CheckInEntity>
)
