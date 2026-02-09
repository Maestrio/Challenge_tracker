package com.example.challengetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.challengetracker.data.AppDatabase
import com.example.challengetracker.data.ChallengeRepository
import com.example.challengetracker.ui.MainViewModel
import com.example.challengetracker.ui.MainViewModelFactory
import com.example.challengetracker.ui.components.CheckInDialog
import com.example.challengetracker.ui.screens.ArchivedChallengesScreen
import com.example.challengetracker.ui.screens.ChallengeDetailScreen
import com.example.challengetracker.ui.screens.CreateChallengeScreen
import com.example.challengetracker.ui.screens.HomeScreen
import com.example.challengetracker.ui.screens.SettingsScreen
import com.example.challengetracker.ui.theme.ChallengeTrackerTheme
import com.example.challengetracker.widget.WidgetConstants

class MainActivity : ComponentActivity() {
    private val pendingCheckInId = mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingCheckInId.value = intent.takeIf { it.action == WidgetConstants.ACTION_CHECK_IN }
            ?.getLongExtra(WidgetConstants.EXTRA_CHALLENGE_ID, -1L)
            ?.takeIf { it != -1L }
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "challenge-tracker.db"
        ).build()
        val repository = ChallengeRepository(database.challengeDao(), database.checkInDao())

        setContent {
            val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(repository))
            ChallengeTrackerApp(viewModel, pendingCheckInId)
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        pendingCheckInId.value = intent.takeIf { it.action == WidgetConstants.ACTION_CHECK_IN }
            ?.getLongExtra(WidgetConstants.EXTRA_CHALLENGE_ID, -1L)
            ?.takeIf { it != -1L }
    }
}

private sealed class Screen {
    data object Home : Screen()
    data object Create : Screen()
    data object Archive : Screen()
    data object Settings : Screen()
    data object Detail : Screen()
}

@Composable
private fun ChallengeTrackerApp(viewModel: MainViewModel, pendingCheckInIdState: MutableState<Long?>) {
    val state by viewModel.state.collectAsState()
    var screen by remember { mutableStateOf<Screen>(Screen.Home) }

    LaunchedEffect(pendingCheckInIdState.value, state.activeChallenges) {
        val id = pendingCheckInIdState.value ?: return@LaunchedEffect
        val challenge = state.activeChallenges.firstOrNull { it.id == id }
        if (challenge != null) {
            viewModel.openCheckIn(challenge)
            pendingCheckInIdState.value = null
        }
    }

    ChallengeTrackerTheme(darkTheme = state.settings.darkModeEnabled) {
        Scaffold(
            bottomBar = {
                if (screen != Screen.Detail) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = screen == Screen.Home,
                            onClick = { screen = Screen.Home },
                            label = { Text("Home") },
                            icon = {}
                        )
                        NavigationBarItem(
                            selected = screen == Screen.Create,
                            onClick = { screen = Screen.Create },
                            label = { Text("Create") },
                            icon = {}
                        )
                        NavigationBarItem(
                            selected = screen == Screen.Archive,
                            onClick = { screen = Screen.Archive },
                            label = { Text("Archive") },
                            icon = {}
                        )
                        NavigationBarItem(
                            selected = screen == Screen.Settings,
                            onClick = { screen = Screen.Settings },
                            label = { Text("Settings") },
                            icon = {}
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                when (screen) {
                    Screen.Home -> {
                        HomeScreen(
                            challenges = state.activeChallenges,
                            onChallengeClick = { viewModel.openCheckIn(it) },
                            onDetailsClick = {
                                viewModel.openDetails(it.id)
                                screen = Screen.Detail
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Screen.Create -> {
                        CreateChallengeScreen(
                            defaultThreshold = state.settings.defaultSuccessThreshold,
                            onCreate = {
                                viewModel.createChallenge(it)
                                screen = Screen.Home
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Screen.Archive -> {
                        ArchivedChallengesScreen(
                            challenges = state.archivedChallenges,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Screen.Settings -> {
                        SettingsScreen(
                            settings = state.settings,
                            onDailyReminderToggle = viewModel::updateDailyReminder,
                            onWeeklyReviewToggle = viewModel::updateWeeklyReview,
                            onThresholdChange = viewModel::updateDefaultThreshold,
                            onDarkModeToggle = viewModel::toggleDarkMode,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Screen.Detail -> {
                        state.selectedDetail?.let { detail ->
                            ChallengeDetailScreen(
                                detail = detail,
                                onArchive = {
                                    viewModel.archiveChallenge(detail.overview.id)
                                    viewModel.closeDetails()
                                    screen = Screen.Home
                                },
                                onDelete = {
                                    viewModel.deleteChallenge(detail.overview.id)
                                    viewModel.closeDetails()
                                    screen = Screen.Home
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }

    state.checkInTarget?.let { challenge ->
        CheckInDialog(
            challengeName = challenge.name,
            onDismiss = viewModel::closeCheckIn,
            onSubmit = { didSucceed, note -> viewModel.submitCheckIn(didSucceed, note) }
        )
    }
}
