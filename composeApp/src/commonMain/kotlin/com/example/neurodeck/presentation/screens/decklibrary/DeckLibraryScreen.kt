package com.example.neurodeck.presentation.screens.decklibrary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.neurodeck.domain.model.Deck
import com.example.neurodeck.presentation.components.EmptyState
import com.example.neurodeck.presentation.components.ErrorMessage
import com.example.neurodeck.presentation.components.LoadingIndicator
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material.icons.filled.Edit

/**
 * Deck Library = halaman utama. List semua deck, FAB untuk bikin deck baru.
 *
 * Navigation contract:
 * - [onDeckClick]: navigate ke detail/study session deck tertentu
 *
 * UI state pattern: collect StateFlow dari ViewModel, exhaustive when di body.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckLibraryScreen(
    onDeckClick: (deckId: Long) -> Unit,
    viewModel: DeckLibraryViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var deckToEdit by remember { mutableStateOf<Deck?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("NeuroDeck") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Buat Deck Baru")
            }
        },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                DeckLibraryUiState.Loading -> LoadingIndicator()

                DeckLibraryUiState.Empty -> EmptyState(
                    emoji = "🃏",
                    title = "Belum Ada Deck",
                    description = "Mulai belajar dengan membuat deck flashcard pertama Anda.",
                    primaryActionLabel = "Buat Deck",
                    onPrimaryAction = { showCreateDialog = true },
                )

                is DeckLibraryUiState.Success -> DeckList(
                    decks = state.decks,
                    onDeckClick = onDeckClick,
                    onEditClick = { deck -> deckToEdit = deck },
                    onDeleteClick = viewModel::deleteDeck,
                )

                is DeckLibraryUiState.Error -> ErrorMessage(
                    message = state.message,
                )
            }
        }
    }

    // Dialog untuk CREATE
    if (showCreateDialog) {
        DeckFormDialog(
            initialDeck = null,
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, description ->
                viewModel.createDeck(title, description) { newId ->
                    showCreateDialog = false
                    onDeckClick(newId)  // langsung navigate ke deck baru
                }
            },
        )
    }

    // Dialog untuk EDIT
    deckToEdit?.let { deck ->
        DeckFormDialog(
            initialDeck = deck,
            onDismiss = { deckToEdit = null },
            onConfirm = { title, description ->
                viewModel.updateDeck(deck, title, description)
                deckToEdit = null
            },
        )
    }
}

/**
 * Daftar deck dalam LazyColumn (efficient scrolling).
 */
@Composable
private fun DeckList(
    decks: List<Deck>,
    onDeckClick: (Long) -> Unit,
    onEditClick: (Deck) -> Unit,
    onDeleteClick: (Long) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = decks, key = { it.id }) { deck ->
            DeckCard(
                deck = deck,
                onClick = { onDeckClick(deck.id) },
                onEdit = { onEditClick(deck) },
                onDelete = { onDeleteClick(deck.id) },
            )
        }
    }
}

@Composable
private fun DeckCard(
    deck: Deck,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = deck.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                if (deck.description.isNotBlank()) {
                    Text(
                        text = deck.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Text(
                    text = "${deck.cardCount} kartu",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Deck",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Hapus Deck",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Hapus Deck?") },
            text = {
                Text(
                    "Deck \"${deck.title}\" dan semua kartunya akan dihapus permanen. " +
                            "Tindakan ini tidak bisa di-undo.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                ) {
                    Text(
                        "Hapus",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Batal")
                }
            },
        )
    }
}

/**
 * Dialog form deck — dipakai untuk CREATE dan EDIT.
 *
 * Mode di-detect dari [initialDeck]:
 * - null    → CREATE mode (field kosong, title "Deck Baru")
 * - non-null → EDIT mode (field pre-filled, title "Edit Deck")
 *
 * Pattern ini menghindari duplikasi UI antara create dan edit.
 */
@Composable
private fun DeckFormDialog(
    initialDeck: Deck?,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String) -> Unit,
) {
    val isEditMode = initialDeck != null
    var title by remember { mutableStateOf(initialDeck?.title ?: "") }
    var description by remember { mutableStateOf(initialDeck?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditMode) "Edit Deck" else "Deck Baru") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi (opsional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title, description) },
                enabled = title.isNotBlank(),
            ) {
                Text(if (isEditMode) "Simpan" else "Buat")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
    )
}