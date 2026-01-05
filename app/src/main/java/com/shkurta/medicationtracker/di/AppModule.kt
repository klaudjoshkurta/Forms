package com.shkurta.medicationtracker.di

import android.content.Context
import androidx.room.Room
import com.shkurta.medicationtracker.data.MedicationDao
import com.shkurta.medicationtracker.data.MedicationDatabase
import com.shkurta.medicationtracker.data.MedicationLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMedicationDatabase(@ApplicationContext context: Context): MedicationDatabase {
        return Room.databaseBuilder(
            context,
            MedicationDatabase::class.java,
            "medication_database"
        )
        .fallbackToDestructiveMigration() // Simple migration strategy for development
        .build()
    }

    @Provides
    @Singleton
    fun provideMedicationDao(database: MedicationDatabase): MedicationDao {
        return database.medicationDao()
    }

    @Provides
    @Singleton
    fun provideMedicationLogDao(database: MedicationDatabase): MedicationLogDao {
        return database.medicationLogDao()
    }
}