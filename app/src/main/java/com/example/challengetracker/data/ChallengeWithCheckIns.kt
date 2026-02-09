package com.example.challengetracker.data

import androidx.room.Embedded
import androidx.room.Relation

data class ChallengeWithCheckIns(
    @Embedded val challenge: ChallengeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "challengeId"
    )
    val checkIns: List<CheckInEntity>
)
