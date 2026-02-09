package com.example.challengetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.challengetracker.model.ChallengeOverview

@Composable
fun ArchivedChallengesScreen(
    challenges: List<ChallengeOverview>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(challenges, key = { it.id }) { challenge ->
            val badge = if (challenge.currentPercent >= challenge.successThreshold) "Success" else "Missed"
            Card {
                Text(
                    text = "${challenge.name} · ${challenge.startDate} → ${challenge.endDate} · ${challenge.currentPercent}% · $badge",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
