package com.example.challengetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.challengetracker.data.ChallengeEntity
import com.example.challengetracker.model.ChallengeType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChallengeScreen(
    defaultThreshold: Int,
    onCreate: (ChallengeEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf(21) }
    var expanded by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(LocalDate.now().plusDays(1).format(dateFormatter)) }
    var threshold by remember { mutableStateOf(defaultThreshold.toFloat()) }
    var isAvoid by remember { mutableStateOf(false) }
    var terminalGoal by remember { mutableStateOf("") }
    var postRule by remember { mutableStateOf("") }

    val durationOptions = listOf(7, 14, 21, 30, 60, 90)

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Create Challenge", style = MaterialTheme.typography.headlineSmall)
        }
        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Challenge name*") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = duration.toString(),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Duration (days)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    durationOptions.forEach { days ->
                        DropdownMenuItem(
                            text = { Text(days.toString()) },
                            onClick = {
                                duration = days
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        item {
            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("Start date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Column {
                Text(text = "Success threshold: ${threshold.toInt()}%")
                Slider(
                    value = threshold,
                    onValueChange = { threshold = it },
                    valueRange = 50f..100f
                )
            }
        }
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isAvoid) "Avoid something" else "Do something")
                Switch(checked = isAvoid, onCheckedChange = { isAvoid = it })
            }
        }
        item {
            OutlinedTextField(
                value = terminalGoal,
                onValueChange = { terminalGoal = it },
                label = { Text("Terminal goal (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = postRule,
                onValueChange = { postRule = it },
                label = { Text("Post-challenge rule (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (name.isBlank()) {
                        return@Button
                    }
                    onCreate(
                        ChallengeEntity(
                            name = name.trim(),
                            description = description.ifBlank { null },
                            durationDays = duration,
                            startDate = startDate,
                            successThreshold = threshold.toInt(),
                            type = if (isAvoid) ChallengeType.AVOID_SOMETHING else ChallengeType.DO_SOMETHING,
                            terminalGoal = terminalGoal.ifBlank { null },
                            postChallengeRule = postRule.ifBlank { null },
                            createdAt = LocalDate.now().format(dateFormatter)
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save challenge")
            }
        }
    }
}
