package com.example.challengetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.challengetracker.model.ChallengeOverview
import com.example.challengetracker.ui.components.ChallengeCard

@Composable
fun HomeScreen(
    challenges: List<ChallengeOverview>,
    onChallengeClick: (ChallengeOverview) -> Unit,
    onDetailsClick: (ChallengeOverview) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (challenges.size > 5) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "Consider focusing on fewer challenges",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(challenges, key = { it.id }) { challenge ->
                ChallengeCard(
                    challenge = challenge,
                    onClick = { onChallengeClick(challenge) },
                    onDetailsClick = { onDetailsClick(challenge) }
                )
            }
        }
    }
}
