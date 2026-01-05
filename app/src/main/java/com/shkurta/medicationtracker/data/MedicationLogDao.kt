package com.shkurta.medicationtracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: MedicationLog)

    @Delete
    suspend fun deleteLog(log: MedicationLog)

    @Query("SELECT * FROM medication_logs WHERE id = :logId")
    suspend fun getLogById(logId: Int): MedicationLog?

    @Query("SELECT * FROM medication_logs WHERE medicationId = :medicationId ORDER BY timestamp DESC")
    fun getLogsForMedication(medicationId: Int): Flow<List<MedicationLog>>

    @Query("SELECT * FROM medication_logs WHERE timestamp BETWEEN :startTime AND :endTime")
    fun getLogsBetween(startTime: Long, endTime: Long): Flow<List<MedicationLog>>
}