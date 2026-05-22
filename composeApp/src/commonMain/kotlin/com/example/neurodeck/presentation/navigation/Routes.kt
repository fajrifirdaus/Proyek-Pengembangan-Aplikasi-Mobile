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

    /**
     * List kartu dalam 1 deck. Argument: deckId.
     * Diakses dari DeckLibrary saat user tap card deck.
     */
    data object CardList : Screen("card_list/{deckId}") {
        fun createRoute(deckId: Long): String = "card_list/$deckId"
    }

    /**
     * Form untuk tambah kartu baru ke deck. Argument: deckId.
     * Diakses dari CardListScreen via FAB.
     */
    data object AddCard : Screen("add_card/{deckId}") {
        fun createRoute(deckId: Long): String = "add_card/$deckId"
    }

    /**
     * Study Session untuk deck tertentu. Argument: deckId.
     * Diakses dari CardListScreen via tombol "Mulai Belajar".
     */
    data object StudySession : Screen("study_session/{deckId}") {
        fun createRoute(deckId: Long): String = "study_session/$deckId"
    }

    /**
     * Form edit kartu existing. Argument: cardId.
     * Diakses dari CardListScreen via icon Edit di CardItem.
     */
    data object EditCard : Screen("edit_card/{cardId}") {
        fun createRoute(cardId: Long): String = "edit_card/$cardId"
    }
}

/**
 * Argument keys — pakai konstanta supaya tidak typo di multiple tempat.
 */
object NavArgs {
    const val DECK_ID = "deckId"
    const val CARD_ID = "cardId"   // ← tambah ini
}