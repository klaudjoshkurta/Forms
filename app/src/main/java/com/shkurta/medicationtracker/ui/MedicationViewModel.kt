package com.shkurta.medicationtracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shkurta.medicationtracker.data.Medication
import com.shkurta.medicationtracker.data.MedicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
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
    val doseNumber: Int,
    val totalDoses: Int
)

data class DaySchedule(
    val date: Long,
    val dayNumber: String, // "04"
    val monthName: String, // "OCT"
    val events: List<TimelineEvent>
)

data class HomeUiState(
    val medications: List<Medication>,
    val dailySchedules: List<DaySchedule>,
    val selectedDate: Long,
    val selectedMonthString: String // "October"
)

@HiltViewModel
class MedicationViewModel @Inject constructor(
    private val repository: MedicationRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    
    private val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
    private val dayNumberFormat = SimpleDateFormat("dd", Locale.getDefault())
    private val monthShortFormat = SimpleDateFormat("MMM", Locale.getDefault())

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeUiState: StateFlow<HomeUiState> = combine(
        repository.allMedications,
        _selectedDate,
        _selectedDate.flatMapLatest { date ->
            repository.getLogsBetween(getStartOfMonth(date), getEndOfMonth(date))
        }
    ) { medications, selectedDate, logs ->
        val medMap = medications.associateBy { it.id }
        
        // Group logs by day and medication to calculate dose numbers
        val logsByMedAndDay = logs.groupBy { 
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.timestamp
            "${cal.get(Calendar.DAY_OF_YEAR)}-${it.medicationId}"
        }

        // Create TimelineEvents
        val allEvents = logs.sortedBy { it.timestamp }.mapNotNull { log ->
            val med = medMap[log.medicationId]
            if (med != null) {
                val cal = Calendar.getInstance()
                cal.timeInMillis = log.timestamp
                val key = "${cal.get(Calendar.DAY_OF_YEAR)}-${log.medicationId}"
                val dayLogs = logsByMedAndDay[key]?.sortedBy { it.timestamp } ?: emptyList()
                val doseNumber = dayLogs.indexOfFirst { it.id == log.id } + 1
                val totalDoses = dayLogs.size

                TimelineEvent(log.id, med, log.timestamp, doseNumber, totalDoses)
            } else {
                null
            }
        }

        // Group by Day
        val groupedEvents = allEvents.groupBy { 
            getStartOfDay(it.timestamp)
        }.map { (dayStart, events) ->
            val date = Date(dayStart)
            DaySchedule(
                date = dayStart,
                dayNumber = dayNumberFormat.format(date),
                monthName = monthShortFormat.format(date).uppercase(Locale.getDefault()),
                events = events.sortedBy { it.timestamp }
            )
        }.sortedBy { it.date }

        HomeUiState(
            medications = medications,
            dailySchedules = groupedEvents,
            selectedDate = selectedDate,
            selectedMonthString = monthFormat.format(Date(selectedDate))
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(emptyList(), emptyList(), System.currentTimeMillis(), "")
    )

    fun selectDate(timestamp: Long) {
        _selectedDate.value = timestamp
    }

    private fun getStartOfDay(time: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getStartOfMonth(time: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfMonth(time: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

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