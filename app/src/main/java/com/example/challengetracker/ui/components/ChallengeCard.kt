package com.example.challengetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.challengetracker.model.ChallengeOverview
import com.example.challengetracker.model.RiskStatus

@Composable
fun ChallengeCard(
    challenge: ChallengeOverview,
    onClick: () -> Unit,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (challenge.riskStatus) {
        RiskStatus.ON_TRACK -> Color(0xFF5BAA71)
        RiskStatus.AT_RISK -> Color(0xFFE3B341)
        RiskStatus.BELOW_THRESHOLD -> Color(0xFFD66C6C)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(text = challenge.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Day ${challenge.day} of ${challenge.totalDays}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${challenge.currentPercent}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = statusColor
                )
                TextButton(onClick = onDetailsClick) {
                    Text(text = "Details")
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = challenge.progressFraction,
            color = statusColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        )
    }
}
