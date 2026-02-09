package com.example.challengetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.challengetracker.data.CheckInEntity
import com.example.challengetracker.model.ChallengeDetail
import com.example.challengetracker.model.calculateProgress
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChallengeDetailScreen(
    detail: ChallengeDetail,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val overview = detail.overview
    val checkIns = detail.checkIns
    val progress = calculateProgress(
        startDate = overview.startDate,
        durationDays = overview.totalDays,
        successThreshold = overview.successThreshold,
        checkIns = checkIns
    )

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(text = overview.name, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "${overview.startDate} → ${overview.endDate}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Terminal goal", style = MaterialTheme.typography.titleSmall)
                    Text(text = overview.terminalGoal.ifBlank { "No goal added" })
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Post-challenge rule", style = MaterialTheme.typography.titleSmall)
                    Text(text = overview.postChallengeRule.ifBlank { "No rule added" })
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Stats", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Total days")
                        Text(text = progress.totalDays.toString())
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Completed")
                        Text(text = progress.completedDays.toString())
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Missed")
                        Text(text = progress.missedDays.toString())
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Current %")
                        Text(text = "${progress.currentPercent}%")
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Streak")
                        Text(text = progress.streak.toString())
                    }
                }
            }
        }
        item {
            Text(text = "Calendar", style = MaterialTheme.typography.titleSmall)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                calendarDays(overview.startDate, overview.totalDays, checkIns).forEach { day ->
                    val color = when (day.state) {
                        DayState.SUCCESS -> Color(0xFF5BAA71)
                        DayState.MISSED -> Color(0xFFD66C6C)
                        DayState.FUTURE -> Color(0xFF9EA7B3)
                        DayState.UNLOGGED -> Color(0xFFF2F2F2)
                    }
                    Column(
                        modifier = Modifier
                            .width(40.dp)
                            .height(40.dp)
                            .background(color, MaterialTheme.shapes.small),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = day.label, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        item {
            Text(text = "Slip notes", style = MaterialTheme.typography.titleSmall)
            val slipNotes = checkIns.filter { it.note?.isNotBlank() == true }
            if (slipNotes.isEmpty()) {
                Text(text = "No notes yet", style = MaterialTheme.typography.bodySmall)
            } else {
                slipNotes.forEach { note ->
                    Text(text = "${note.date}: ${note.note}")
                }
            }
        }
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = onArchive, modifier = Modifier.weight(1f)) {
                    Text(text = "Archive")
                }
                Button(onClick = onDelete, modifier = Modifier.weight(1f)) {
                    Text(text = "Delete")
                }
            }
        }
    }
}

private enum class DayState {
    SUCCESS,
    MISSED,
    FUTURE,
    UNLOGGED
}

private data class CalendarDay(val label: String, val state: DayState)

private fun calendarDays(
    startDate: LocalDate,
    durationDays: Int,
    checkIns: List<CheckInEntity>
): List<CalendarDay> {
    val checkInMap = checkIns.associateBy { LocalDate.parse(it.date) }
    val today = LocalDate.now()
    return (0 until durationDays).map { index ->
        val date = startDate.plusDays(index.toLong())
        val state = when {
            date.isAfter(today) -> DayState.FUTURE
            checkInMap[date]?.didSucceed == true -> DayState.SUCCESS
            checkInMap[date]?.didSucceed == false -> DayState.MISSED
            else -> DayState.UNLOGGED
        }
        CalendarDay(label = (index + 1).toString(), state = state)
    }
}
