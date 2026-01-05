package com.shkurta.medicationtracker.ui.screen

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shkurta.medicationtracker.data.Medication
import com.shkurta.medicationtracker.ui.DaySchedule
import com.shkurta.medicationtracker.ui.MedicationViewModel
import com.shkurta.medicationtracker.ui.TimelineEvent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Design System Colors
val PrimaryPink = Color(0xFFE91E63)
val TextDark = Color(0xFF212121)
val TextLight = Color(0xFF757575)
val DividerColor = Color(0xFFEEEEEE)
val BackgroundWhite = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MedicationViewModel = hiltViewModel()
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    // For date picker
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = homeUiState.selectedDate

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val newCalendar = Calendar.getInstance()
            newCalendar.set(year, month, dayOfMonth)
            viewModel.selectDate(newCalendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Amily Watson",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                        )
                    },
                    navigationIcon = {
                        Box(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )
                    },
                    actions = {
                        IconButton(onClick = { /* Settings */ }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = TextDark)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = BackgroundWhite
                    )
                )
                
                // Month Selector Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Month (Dummy)
                    Text(
                        text = "September",
                        style = TextStyle(fontSize = 14.sp, color = Color.LightGray)
                    )
                    
                    // Current Month (Active)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = homeUiState.selectedMonthString, // "October"
                            style = TextStyle(
                                fontSize = 16.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = PrimaryPink
                            )
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .width(24.dp)
                                .height(2.dp)
                                .background(PrimaryPink)
                        )
                    }
                    
                    // Next Month (Dummy)
                    Text(
                        text = "November",
                        style = TextStyle(fontSize = 14.sp, color = Color.LightGray)
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = BackgroundWhite,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Icon(Icons.Default.MedicalServices, contentDescription = "Meds", tint = Color.LightGray) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Schedule", tint = PrimaryPink) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = BackgroundWhite
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.LightGray) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryPink,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp) // Removed spacing, handled by items
        ) {
            items(homeUiState.dailySchedules) { daySchedule ->
                DayScheduleItem(daySchedule)
            }
            
            if (homeUiState.dailySchedules.isEmpty()) {
                 item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No schedule for this month", color = TextLight)
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
    }
}

@Composable
fun DayScheduleItem(daySchedule: DaySchedule) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp)
    ) {
        // Left Date Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(60.dp)
        ) {
            Text(
                text = daySchedule.dayNumber, // "04"
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Thin,
                    color = TextDark // Or PrimaryPink if active day
                )
            )
            Text(
                text = daySchedule.monthName, // "OCT"
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark // Or PrimaryPink
                )
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
        
        // Vertical Divider Line
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight() // This needs intrinsic height to work perfectly, simpler to fix height or rely on content
                .height(100.dp) // Approximate for now or calculate based on list size
                .background(DividerColor)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Events List for this Day
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            daySchedule.events.forEach { event ->
                EventItem(event)
            }
        }
    }
    
    // Horizontal Divider between days
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(DividerColor)
    )
}

@Composable
fun EventItem(event: TimelineEvent) {
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) } // 9:00 PM

    Row(verticalAlignment = Alignment.Top) {
        // Dot indicator
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E0E0)) // Light grey dot
        )
        
        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = timeFormat.format(Date(event.timestamp)),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${event.medication.name} - ${event.medication.dosage}",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = TextLight
                )
            )
        }
    }
}

@Composable
fun AddMedicationDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }

    AlertDialog(
        containerColor = BackgroundWhite,
        titleContentColor = TextDark,
        textContentColor = TextDark,
        onDismissRequest = onDismiss,
        title = { Text("Add Medication") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name, 
                    onValueChange = { name = it }, 
                    label = { Text("Name") },
                )
                OutlinedTextField(
                    value = dosage, 
                    onValueChange = { dosage = it }, 
                    label = { Text("Dosage") },
                )
                OutlinedTextField(
                    value = frequency, 
                    onValueChange = { frequency = it }, 
                    label = { Text("Frequency") },
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && dosage.isNotBlank() && frequency.isNotBlank()) {
                        onConfirm(name, dosage, frequency)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = TextLight)
            ) {
                Text("Cancel")
            }
        }
    )
}