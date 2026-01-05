package com.shkurta.medicationtracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medication_logs",
    foreignKeys = [
        ForeignKey(
            entity = Medication::class,
            parentColumns = ["id"],
            childColumns = ["medicationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["medicationId"])]
)
data class MedicationLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val medicationId: Int,
    val timestamp: Long // Unix timestamp of when it was taken
)