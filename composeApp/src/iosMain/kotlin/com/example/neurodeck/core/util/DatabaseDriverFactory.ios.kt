package com.example.neurodeck.core.util

import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        // TODO Sprint 2: implementasi NativeSqliteDriver setelah schema ada.
        throw NotImplementedError(
            "Database schema belum dibuat. Akan diimplementasikan setelah Deck.sq dan Card.sq ditambahkan."
        )
    }
}