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
import com.example.neurodeck.presentation.screens.addcard.AddCardScreen
import com.example.neurodeck.presentation.screens.cardlist.CardListScreen
import com.example.neurodeck.presentation.screens.decklibrary.DeckLibraryScreen
import com.example.neurodeck.presentation.screens.studysession.StudySessionScreen
import com.example.neurodeck.presentation.screens.editcard.EditCardScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.DeckLibrary.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        // ============================================================
        // 1. DECK LIBRARY (Halaman Utama)
        // ============================================================
        composable(route = Screen.DeckLibrary.route) {
            DeckLibraryScreen(
                onDeckClick = { deckId ->
                    // Tap deck → ke CardList (lihat semua kartu di deck)
                    navController.navigate(Screen.CardList.createRoute(deckId))
                },
            )
        }

        // ============================================================
        // 2. CARD LIST (Kelola kartu di 1 deck)
        // ============================================================
        composable(
            route = Screen.CardList.route,
            arguments = listOf(
                navArgument(NavArgs.DECK_ID) {
                    type = NavType.LongType
                },
            ),
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getLong(NavArgs.DECK_ID) ?: 0L
            CardListScreen(
                deckId = deckId,
                onAddCard = { id -> navController.navigate(Screen.AddCard.createRoute(id)) },
                onEditCard = { cardId -> navController.navigate(Screen.EditCard.createRoute(cardId)) },
                onStartStudy = { id -> navController.navigate(Screen.StudySession.createRoute(id)) },
                onBack = { navController.popBackStack() },
            )
        }

        // ============================================================
        // 3. ADD CARD (Form tambah kartu baru)
        // ============================================================
        composable(
            route = Screen.AddCard.route,
            arguments = listOf(
                navArgument(NavArgs.DECK_ID) {
                    type = NavType.LongType
                },
            ),
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getLong(NavArgs.DECK_ID) ?: 0L
            AddCardScreen(
                deckId = deckId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        // ============================================================
        // 4. STUDY SESSION (Belajar dengan kartu yang due)
        // ============================================================
        composable(
            route = Screen.StudySession.route,
            arguments = listOf(
                navArgument(NavArgs.DECK_ID) {
                    type = NavType.LongType
                },
            ),
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getLong(NavArgs.DECK_ID) ?: 0L
            StudySessionScreen(
                deckId = deckId,
                onExit = { navController.popBackStack() },
            )
        }

        // ============================================================
        // EDIT CARD (Edit kartu existing)
        // ============================================================
        composable(
            route = Screen.EditCard.route,
            arguments = listOf(
                navArgument(NavArgs.CARD_ID) {
                    type = NavType.LongType
                },
            ),
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getLong(NavArgs.CARD_ID) ?: 0L
            EditCardScreen(
                cardId = cardId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        // ============================================================
        // 5. PLACEHOLDER ROUTES (akan di-implement di Sprint 3+)
        // ============================================================
        composable(route = Screen.ImportGenerate.route) {
            PlaceholderScreen(label = "Import & Generate (AI) — Sprint 3")
        }

        composable(route = Screen.Statistics.route) {
            PlaceholderScreen(label = "Statistics — Sprint 2/3")
        }

        composable(route = Screen.Settings.route) {
            PlaceholderScreen(label = "Settings — Sprint 4")
        }
    }
}

/**
 * Helper composable untuk route yang belum di-implement.
 * Lebih clean daripada duplicate Box+Text di setiap composable.
 */
@Composable
private fun PlaceholderScreen(label: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Placeholder: $label")
    }
}