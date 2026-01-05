package com.shkurta.medicationtracker.ui.screen

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shkurta.medicationtracker.R
import com.shkurta.medicationtracker.data.Medication
import com.shkurta.medicationtracker.ui.DaySchedule
import com.shkurta.medicationtracker.ui.MedicationViewModel
import com.shkurta.medicationtracker.ui.TimelineEvent
import java.text.SimpleDateFormat
import java.util.*

// --- Colors ---
val PrimaryPink = Color(0xFFE91E63)
val TextDark = Color(0xFF212121)
val TextLight = Color(0xFF757575)
val DividerColor = Color(0xFFEEEEEE)
val BackgroundWhite = Color(0xFFFFFFFF)

// Dashboard Colors
val MorningCyan = Color(0xFF80DEEA)
val AfternoonPink = Color(0xFFF48FB1) // Lighter pink for card
val EveningYellow = Color(0xFFFFF59D)
val NightBlue = Color(0xFF9FA8DA)
val FavCardPurple = Color(0xFF7986CB)

// Profile Colors
val StatCyan = Color(0xFF26C6DA)
val StatGreen = Color(0xFF9CCC65)
val StatPurple = Color(0xFF7E57C2)

// --- Main Container ---

@Composable
fun HomeScreen(
    viewModel: MedicationViewModel = hiltViewModel()
) {
    var currentTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = BackgroundWhite,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { 
                        Icon(
                            imageVector = Icons.Filled.Medication, // Pill icon
                            contentDescription = "My Medicines",
                            tint = if (currentTab == 0) PrimaryPink else Color.LightGray
                        ) 
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = BackgroundWhite)
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { 
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth, 
                            contentDescription = "Schedule",
                            tint = if (currentTab == 1) PrimaryPink else Color.LightGray
                        ) 
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = BackgroundWhite)
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = { 
                        Icon(
                            imageVector = Icons.Filled.Favorite, // Heart icon
                            contentDescription = "Profile",
                            tint = if (currentTab == 2) PrimaryPink else Color.LightGray
                        ) 
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = BackgroundWhite)
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                0 -> DashboardScreen()
                1 -> ScheduleScreen(viewModel)
                2 -> ProfileScreen()
            }
        }
    }
}

// --- Screen 1: Dashboard (My Medicines) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                         // Avatar Placeholder
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Amily Watson",
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextDark)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "MY MEDICINES",
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextLight, letterSpacing = 1.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // 2x2 Grid
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MedicineCategoryCard(
                        modifier = Modifier.weight(1f),
                        title = "Morning",
                        subtitle = "pills",
                        color = MorningCyan,
                        icon = Icons.Outlined.WbSunny
                    )
                    MedicineCategoryCard(
                        modifier = Modifier.weight(1f),
                        title = "Afternoon",
                        subtitle = "",
                        color = AfternoonPink, // Using a lighter pink/white with pink border style if needed, but sticking to block color for simplicity
                        icon = Icons.Outlined.WbTwilight
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MedicineCategoryCard(
                        modifier = Modifier.weight(1f),
                        title = "Evening",
                        subtitle = "",
                        color = EveningYellow,
                        icon = Icons.Outlined.NightsStay
                    )
                    MedicineCategoryCard(
                        modifier = Modifier.weight(1f),
                        title = "Night",
                        subtitle = "",
                        color = NightBlue,
                        icon = Icons.Outlined.Bedtime
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "FAVOURITES",
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextLight, letterSpacing = 1.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Favourites Card
            Card(
                colors = CardDefaults.cardColors(containerColor = FavCardPurple),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(80.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.Gray)
                    ) // Placeholder for Jayeon
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Jayeon", style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp))
                        Text("Late for 24 min", style = TextStyle(color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Filled.Medication, contentDescription = null, tint = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // List Item
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.LightGray)
                ) // Placeholder for Jacqueline
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Jacqueline", style = TextStyle(color = TextDark, fontWeight = FontWeight.Bold, fontSize = 16.sp))
                    Text("OK", style = TextStyle(color = TextLight, fontSize = 12.sp))
                }
            }
        }
    }
}

@Composable
fun MedicineCategoryCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    color: Color,
    icon: ImageVector
) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = TextStyle(color = Color.White, fontWeight = FontWeight.SemiBold))
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
    }
}

// --- Screen 3: Profile ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("") },
                navigationIcon = {
                     IconButton(onClick = {}) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = TextDark)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextDark)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header
             Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("AMILY WATSON", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark))
            Text("ID: 24097", style = TextStyle(fontSize = 12.sp, color = TextLight))
            Spacer(modifier = Modifier.height(8.dp))
            Text("EDIT PROFILE", style = TextStyle(fontSize = 12.sp, color = TextLight, fontWeight = FontWeight.Medium))
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Nutrients", style = TextStyle(color = TextLight))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Vitamins", style = TextStyle(color = PrimaryPink, fontWeight = FontWeight.Bold))
                    Box(modifier = Modifier.width(40.dp).height(2.dp).background(PrimaryPink))
                }
                Text("Minerals", style = TextStyle(color = TextLight))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Stats List
            StatItem(name = "Vitamin B", desc = "Raw cantaloupe is good source...", percentage = 54, color = StatCyan)
            Spacer(modifier = Modifier.height(16.dp))
            StatItem(name = "Vitamin C", desc = "Raw pineapple has 10mg...", percentage = 81, color = StatGreen)
            Spacer(modifier = Modifier.height(16.dp))
            StatItem(name = "Vitamin D", desc = "You can get vitamin D from...", percentage = 60, color = StatPurple)
        }
    }
}

@Composable
fun StatItem(name: String, desc: String, percentage: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
            CircularProgressIndicator(
                progress = { percentage / 100f },
                modifier = Modifier.fillMaxSize(),
                color = color,
                trackColor = Color(0xFFEEEEEE),
            )
            Text("$percentage%", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark))
            Text(desc, style = TextStyle(fontSize = 12.sp, color = TextLight), maxLines = 2)
        }
    }
}

// --- Screen 5: Schedule (Previously HomeScreen) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: MedicationViewModel
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
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextDark)
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
                                color = PrimaryPink,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.clickable { datePickerDialog.show() }
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
            verticalArrangement = Arrangement.spacedBy(0.dp)
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
                    color = PrimaryPink
                )
            )
            Text(
                text = daySchedule.monthName, // "OCT"
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryPink
                )
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
        
        // Vertical Divider Line
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(100.dp) 
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