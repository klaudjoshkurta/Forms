package com.shkurta.medicationtracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shkurta.medicationtracker.data.Medication
import com.shkurta.medicationtracker.ui.MedicationViewModel
import com.shkurta.medicationtracker.ui.TimelineEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: MedicationViewModel = hiltViewModel()
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showTakeDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FloatingActionButton(
                    onClick = { showTakeDialog = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Log Dose")
                }
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Medication")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (homeUiState.todayEvents.isEmpty() && homeUiState.yesterdayEvents.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No medication history yet")
                    }
                }
            } else {
                if (homeUiState.todayEvents.isNotEmpty()) {
                    item {
                        TimelineHeader(homeUiState.todayDateString)
                    }
                    items(homeUiState.todayEvents) { event ->
                        TimelineItem(
                            event = event,
                            onDelete = { viewModel.deleteEvent(event) }
                        )
                    }
                }

                if (homeUiState.yesterdayEvents.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        TimelineHeader(homeUiState.yesterdayDateString)
                    }
                    items(homeUiState.yesterdayEvents) { event ->
                        TimelineItem(
                            event = event,
                            onDelete = { viewModel.deleteEvent(event) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddMedicationDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, dosage, frequency ->
                    viewModel.addMedication(name, dosage, frequency)
                    showAddDialog = false
                }
            )
        }

        if (showTakeDialog) {
            TakeMedicationDialog(
                medications = homeUiState.medications,
                onDismiss = { showTakeDialog = false },
                onConfirm = { medication ->
                    viewModel.takeMedication(medication)
                    showTakeDialog = false
                },
                onDelete = { medication ->
                    viewModel.deleteMedication(medication)
                }
            )
        }
    }
}

@Composable
fun TimelineHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun TimelineItem(event: TimelineEvent, onDelete: () -> Unit) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Log?") },
            text = { Text("Are you sure you want to remove this entry?") },
            confirmButton = {
                Button(onClick = {
                    onDelete()
                    showDeleteConfirm = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) 
    ) {
        // Timeline line and dot
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Card Content
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.medication.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${event.medication.dosage} • ${event.medication.frequency}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        // Dose Tag (e.g., "Dose 1", "Dose 2")
                        if (event.totalDoses > 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Dose ${event.doseNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = timeFormat.format(Date(event.timestamp)),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close, 
                            contentDescription = "Remove Entry",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakeMedicationDialog(
    medications: List<Medication>,
    onDismiss: () -> Unit,
    onConfirm: (Medication) -> Unit,
    onDelete: (Medication) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Dose") },
        text = {
            if (medications.isEmpty()) {
                Text("No medications available. Add one first.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(medications) { medication ->
                        Card(
                            onClick = { onConfirm(medication) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = medication.name, style = MaterialTheme.typography.titleMedium)
                                    Text(text = medication.dosage, style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { onDelete(medication) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Medication")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}


@Composable
fun AddMedicationDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Medication") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = dosage, onValueChange = { dosage = it }, label = { Text("Dosage") })
                OutlinedTextField(value = frequency, onValueChange = { frequency = it }, label = { Text("Frequency") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && dosage.isNotBlank() && frequency.isNotBlank()) {
                        onConfirm(name, dosage, frequency)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}