package com.shkurta.forms

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shkurta.forms.ui.screen.HabitsFormScreen
import com.shkurta.forms.ui.screen.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Habits : Screen("habits")
}

@Composable
fun FormsApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController
            )
        }
        composable(Screen.Habits.route) {
            HabitsFormScreen(
                navController = navController
            )
        }
    }
}
