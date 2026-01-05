package com.shkurta.medicationtracker.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shkurta.medicationtracker.ui.screen.HomeScreen

@Composable
fun MedTrackerApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(
            route = Screen.Home.route
        ) {
            HomeScreen()
        }
    }
}
sealed class Screen(val route: String) {
    object Home : Screen("home")
}