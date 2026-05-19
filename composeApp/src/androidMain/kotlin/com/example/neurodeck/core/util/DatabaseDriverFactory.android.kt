package com.example.neurodeck.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        // TODO Sprint 2: ganti NeuroDeckDatabase.Schema dan nama DB setelah schema dibuat.
        // Untuk sementara return stub driver tanpa schema agar Koin tidak crash di runtime.
        // Catatan: jangan pernah ambil database via Koin sebelum schema ada.
        throw NotImplementedError(
            "Database schema belum dibuat. Akan diimplementasikan setelah Deck.sq dan Card.sq ditambahkan."
        )
    }
}