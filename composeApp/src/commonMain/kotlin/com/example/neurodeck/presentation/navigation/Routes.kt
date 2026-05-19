package com.example.neurodeck.presentation.navigation

sealed class Screen(val route: String) {
    object DeckLibrary : Screen("deck_library")

    object ImportGenerate : Screen("import_generate")

    object StudySession : Screen("study_session/{deckId}") {
        fun createRoute(deckId: String): String {
            return "study_session/$deckId"
        }
    }

    object Statistics : Screen("statistics")

    object Settings : Screen("settings")
}