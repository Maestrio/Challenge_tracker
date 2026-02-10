package com.example.challengetracker.data

import androidx.room.TypeConverter
import com.example.challengetracker.model.ChallengeType

class Converters {
    @TypeConverter
    fun toChallengeType(value: String): ChallengeType = ChallengeType.valueOf(value)

    @TypeConverter
    fun fromChallengeType(type: ChallengeType): String = type.name
}
