package com.shkurta.medicationtracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shkurta.medicationtracker.data.Medication
import com.shkurta.medicationtracker.data.MedicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class TimelineEvent(
    val logId: Int,
    val medication: Medication,
    val timestamp: Long,
    val doseNumber: Int,    // Current dose number for this day (1st, 2nd, etc.)
    val totalDoses: Int     // Total doses taken so far today for this med
)

data class HomeUiState(
    val medications: List<Medication>,
    val todayEvents: List<TimelineEvent>,
    val yesterdayEvents: List<TimelineEvent>,
    val todayDateString: String,
    val yesterdayDateString: String
)

@HiltViewModel
class MedicationViewModel @Inject constructor(
    private val repository: MedicationRepository
) : ViewModel() {

    private val startOfYesterday: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }
    
    private val startOfDay: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }

    private val endOfDay: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            return calendar.timeInMillis
        }

    // Date Formatters
    private val headerDateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())

    val homeUiState: StateFlow<HomeUiState> = combine(
        repository.allMedications,
        repository.getLogsBetween(startOfYesterday, endOfDay)
    ) { medications, logs ->
        val medMap = medications.associateBy { it.id }
        
        // Group logs by day and medication to calculate dose numbers
        val logsByDayAndMed = logs.groupBy { 
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.timestamp
            // Create a unique key for "Day-MedID"
            "${cal.get(Calendar.DAY_OF_YEAR)}-${it.medicationId}"
        }

        // Process logs into TimelineEvents with dose info
        // We sort by timestamp ascending first to assign dose numbers correctly (1, 2, 3...)
        val events = logs.sortedBy { it.timestamp }.map { log ->
            val med = medMap[log.medicationId]
            if (med != null) {
                val cal = Calendar.getInstance()
                cal.timeInMillis = log.timestamp
                val dayKey = "${cal.get(Calendar.DAY_OF_YEAR)}-${log.medicationId}"
                
                // Get all logs for this med on this day, sorted by time
                val dayLogs = logsByDayAndMed[dayKey]?.sortedBy { it.timestamp } ?: emptyList()
                
                // Find index of current log (0-based) and add 1 for dose number
                val doseNumber = dayLogs.indexOfFirst { it.id == log.id } + 1
                val totalDoses = dayLogs.size

                TimelineEvent(log.id, med, log.timestamp, doseNumber, totalDoses)
            } else {
                null
            }
        }.filterNotNull().sortedByDescending { it.timestamp } // Sort descending for display

        val todayStart = startOfDay
        val (today, yesterday) = events.partition { it.timestamp >= todayStart }
        
        // Calculate date strings dynamically
        val todayDate = Date()
        val yesterdayDate = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)

        HomeUiState(
            medications = medications,
            todayEvents = today,
            yesterdayEvents = yesterday,
            todayDateString = "Today, ${headerDateFormat.format(todayDate)}",
            yesterdayDateString = "Yesterday, ${headerDateFormat.format(yesterdayDate)}"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(
            emptyList(), 
            emptyList(), 
            emptyList(),
            "Today",
            "Yesterday"
        )
    )

    fun addMedication(name: String, dosage: String, frequency: String) {
        viewModelScope.launch {
            val medication = Medication(name = name, dosage = dosage, frequency = frequency)
            repository.insertMedication(medication)
        }
    }

    fun deleteMedication(medication: Medication) {
        viewModelScope.launch {
            repository.deleteMedication(medication)
        }
    }

    fun takeMedication(medication: Medication) {
        viewModelScope.launch {
            repository.logMedicationTaken(medication.id, System.currentTimeMillis())
        }
    }

    fun deleteEvent(event: TimelineEvent) {
        viewModelScope.launch {
            repository.deleteLog(event.logId)
        }
    }
}