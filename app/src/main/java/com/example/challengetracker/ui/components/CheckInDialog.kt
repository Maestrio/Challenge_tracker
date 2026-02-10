package com.example.challengetracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInDialog(
    challengeName: String,
    onDismiss: () -> Unit,
    onSubmit: (Boolean, String?) -> Unit
) {
    val noteState = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Did you do it today?") },
        text = {
            Column {
                Text(
                    text = challengeName,
                    style = MaterialTheme.typography.titleSmall
                )
                OutlinedTextField(
                    value = noteState.value,
                    onValueChange = { noteState.value = it },
                    label = { Text("What happened? (optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(true, noteState.value) }) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = { onSubmit(false, noteState.value) }) {
                Text("No")
            }
        }
    )
}
