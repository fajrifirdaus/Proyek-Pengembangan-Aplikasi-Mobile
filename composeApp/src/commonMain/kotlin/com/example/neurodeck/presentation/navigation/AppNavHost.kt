package com.example.neurodeck.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.DeckLibrary.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 1. Screen Library Deck (Halaman Utama)
        composable(route = Screen.DeckLibrary.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Placeholder: Deck Library Screen")
            }
        }

        // 2. Screen Import & Generate Flashcard
        composable(route = Screen.ImportGenerate.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Placeholder: Import & Generate Screen")
            }
        }

        // 3. Screen Study Session
        composable(
            route = Screen.StudySession.route,
            arguments = listOf(
                navArgument("deckId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId") ?: ""
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Placeholder: Study Session Screen untuk Deck ID: $deckId")
            }
        }

        // 4. Screen Statistics
        composable(route = Screen.Statistics.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Placeholder: Statistics Screen")
            }
        }

        // 5. Screen Settings
        composable(route = Screen.Settings.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Placeholder: Settings Screen")
            }
        }
    }
}