package com.example.challengetracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.challengetracker.data.ChallengeEntity
import com.example.challengetracker.data.ChallengeRepository
import com.example.challengetracker.data.CheckInEntity
import com.example.challengetracker.model.ChallengeDetail
import com.example.challengetracker.model.ChallengeOverview
import com.example.challengetracker.model.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

data class ChallengeTrackerState(
    val activeChallenges: List<ChallengeOverview> = emptyList(),
    val archivedChallenges: List<ChallengeOverview> = emptyList(),
    val settings: SettingsState = SettingsState(),
    val checkInTarget: ChallengeOverview? = null,
    val selectedDetail: ChallengeDetail? = null
)

class MainViewModel(private val repository: ChallengeRepository) : ViewModel() {
    private val settingsState = MutableStateFlow(SettingsState())
    private val checkInTarget = MutableStateFlow<ChallengeOverview?>(null)
    private val selectedChallengeId = MutableStateFlow<Long?>(null)
    private val detailState = selectedChallengeId.flatMapLatest { id ->
        if (id == null) {
            flowOf(null)
        } else {
            repository.observeChallengeDetail(id)
        }
    }

    val state: StateFlow<ChallengeTrackerState> = combine(
        repository.observeActiveChallenges(),
        repository.observeArchivedChallenges(),
        settingsState,
        checkInTarget,
        detailState
    ) { active, archived, settings, target, detail ->
        ChallengeTrackerState(
            activeChallenges = active,
            archivedChallenges = archived,
            settings = settings,
            checkInTarget = target,
            selectedDetail = detail
        )
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000), ChallengeTrackerState())

    fun openCheckIn(challenge: ChallengeOverview) {
        checkInTarget.value = challenge
    }

    fun closeCheckIn() {
        checkInTarget.value = null
    }

    fun openDetails(challengeId: Long) {
        selectedChallengeId.value = challengeId
    }

    fun closeDetails() {
        selectedChallengeId.value = null
    }

    fun submitCheckIn(didSucceed: Boolean, note: String?) {
        val challenge = checkInTarget.value ?: return
        viewModelScope.launch {
            val checkIn = CheckInEntity(
                challengeId = challenge.id,
                date = LocalDate.now().format(dateFormatter),
                didSucceed = didSucceed,
                note = note?.ifBlank { null }
            )
            repository.addCheckIn(checkIn)
            checkInTarget.value = null
        }
    }

    fun submitCheckInForDate(challengeId: Long, date: String, didSucceed: Boolean, note: String?) {
        if (date.isBlank()) return
        viewModelScope.launch {
            val checkIn = CheckInEntity(
                challengeId = challengeId,
                date = date.trim(),
                didSucceed = didSucceed,
                note = note?.ifBlank { null }
            )
            repository.addCheckIn(checkIn)
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        settingsState.update { it.copy(darkModeEnabled = enabled) }
    }

    fun updateDefaultThreshold(value: Int) {
        settingsState.update { it.copy(defaultSuccessThreshold = value) }
    }

    fun updateDailyReminder(enabled: Boolean) {
        settingsState.update { it.copy(dailyReminderEnabled = enabled) }
    }

    fun updateDailyReminderTime(time: LocalTime) {
        settingsState.update { it.copy(dailyReminderTime = time) }
    }

    fun updateWeeklyReview(enabled: Boolean) {
        settingsState.update { it.copy(weeklyReviewEnabled = enabled) }
    }

    fun updateWeeklyReviewDay(day: DayOfWeek) {
        settingsState.update { it.copy(weeklyReviewDay = day) }
    }

    fun createChallenge(challenge: ChallengeEntity) {
        viewModelScope.launch {
            repository.createChallenge(challenge)
        }
    }

    fun archiveChallenge(id: Long) {
        viewModelScope.launch {
            repository.archiveChallenge(id)
        }
    }

    fun deleteChallenge(id: Long) {
        viewModelScope.launch {
            repository.deleteChallenge(id)
        }
    }
}

class MainViewModelFactory(private val repository: ChallengeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
