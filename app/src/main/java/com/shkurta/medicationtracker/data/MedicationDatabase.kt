package com.shkurta.medicationtracker.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Medication::class, MedicationLog::class], version = 2, exportSchema = false)
abstract class MedicationDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
    abstract fun medicationLogDao(): MedicationLogDao
}