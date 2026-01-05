package com.shkurta.medicationtracker.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MedicationRepository @Inject constructor(
    private val medicationDao: MedicationDao,
    private val medicationLogDao: MedicationLogDao
) {

    val allMedications: Flow<List<Medication>> = medicationDao.getAllMedications()

    suspend fun insertMedication(medication: Medication) {
        medicationDao.insertMedication(medication)
    }

    suspend fun deleteMedication(medication: Medication) {
        medicationDao.deleteMedication(medication)
    }

    suspend fun logMedicationTaken(medicationId: Int, timestamp: Long) {
        val log = MedicationLog(medicationId = medicationId, timestamp = timestamp)
        medicationLogDao.insertLog(log)
    }

    suspend fun deleteLog(logId: Int) {
        val log = medicationLogDao.getLogById(logId)
        if (log != null) {
            medicationLogDao.deleteLog(log)
        }
    }

    fun getLogsForMedication(medicationId: Int): Flow<List<MedicationLog>> {
        return medicationLogDao.getLogsForMedication(medicationId)
    }

    fun getLogsBetween(startTime: Long, endTime: Long): Flow<List<MedicationLog>> {
        return medicationLogDao.getLogsBetween(startTime, endTime)
    }

    fun getAllLogs(): Flow<List<MedicationLog>> {
        return medicationLogDao.getAllLogs()
    }
}