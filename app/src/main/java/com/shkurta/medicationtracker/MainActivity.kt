package com.shkurta.medicationtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.shkurta.medicationtracker.ui.MedTrackerApp
import com.shkurta.medicationtracker.ui.theme.MedicationTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedicationTrackerTheme {
                MedTrackerApp()
            }
        }
    }
}