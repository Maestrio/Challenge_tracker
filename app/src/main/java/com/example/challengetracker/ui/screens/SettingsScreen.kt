package com.example.challengetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.challengetracker.model.SettingsState

@Composable
fun SettingsScreen(
    settings: SettingsState,
    onDailyReminderToggle: (Boolean) -> Unit,
    onWeeklyReviewToggle: (Boolean) -> Unit,
    onThresholdChange: (Int) -> Unit,
    onDarkModeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val thresholdState = remember(settings.defaultSuccessThreshold) {
        mutableStateOf(settings.defaultSuccessThreshold.toString())
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.headlineSmall)

        SettingToggleRow(
            label = "Daily reminder",
            checked = settings.dailyReminderEnabled,
            onCheckedChange = onDailyReminderToggle
        )
        OutlinedTextField(
            value = settings.dailyReminderTime.toString(),
            onValueChange = { },
            label = { Text("Daily reminder time") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        SettingToggleRow(
            label = "Weekly review reminder (Sunday)",
            checked = settings.weeklyReviewEnabled,
            onCheckedChange = onWeeklyReviewToggle
        )
        OutlinedTextField(
            value = settings.weeklyReviewDay.name.lowercase().replaceFirstChar { it.uppercase() },
            onValueChange = { },
            label = { Text("Weekly review day") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        SettingToggleRow(
            label = "Dark mode",
            checked = settings.darkModeEnabled,
            onCheckedChange = onDarkModeToggle
        )

        OutlinedTextField(
            value = thresholdState.value,
            onValueChange = {
                thresholdState.value = it
                it.toIntOrNull()?.let(onThresholdChange)
            },
            label = { Text("Default success threshold") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { }) {
            Text(text = "Export data (JSON)")
        }
    }
}

@Composable
private fun SettingToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
