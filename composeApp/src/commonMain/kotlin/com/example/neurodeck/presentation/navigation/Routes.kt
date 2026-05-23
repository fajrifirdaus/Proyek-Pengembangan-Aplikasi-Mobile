package com.example.neurodeck.presentation.navigation

/**
 * Type-safe route definitions untuk NavHost.
 *
 * Convention: setiap Screen punya `route` (template path) dan optional
 * `createRoute(args)` untuk build URL dengan argument value.
 *
 * Catatan: argument deckId dipakai sebagai Long (sesuai database ID type).
 * createRoute() terima Long, jadi caller cukup kasih ID-nya tanpa convert manual.
 */
sealed class Screen(val route: String) {

    data object DeckLibrary : Screen("deck_library")

    data object ImportGenerate : Screen("import_generate")

    data object Statistics : Screen("statistics")

    data object Settings : Screen("settings")

    data object CardList : Screen("card_list/{deckId}") {
        fun createRoute(deckId: Long): String = "card_list/$deckId"
    }

    data object AddCard : Screen("add_card/{deckId}") {
        fun createRoute(deckId: Long): String = "add_card/$deckId"
    }

    data object StudySession : Screen("study_session/{deckId}") {
        fun createRoute(deckId: Long): String = "study_session/$deckId"
    }
}

object NavArgs {
    const val DECK_ID = "deckId"
}
