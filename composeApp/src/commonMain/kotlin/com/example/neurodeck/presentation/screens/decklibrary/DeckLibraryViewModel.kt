package com.example.neurodeck.presentation.screens.decklibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.model.Deck
import com.example.neurodeck.domain.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * State untuk Deck Library screen.
 *
 * Sealed interface = exhaustive when di Compose, compiler memaksa handle semua case.
 * Lebih aman dari class biasa dengan `isLoading: Boolean` + `error: String?` yang
 * bisa kombinasi invalid (loading=true tapi error juga di-set).
 */
sealed interface DeckLibraryUiState {
    data object Loading : DeckLibraryUiState
    data object Empty : DeckLibraryUiState
    data class Success(val decks: List<Deck>) : DeckLibraryUiState
    data class Error(val message: String) : DeckLibraryUiState
}

class DeckLibraryViewModel(
    private val repository: DeckRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DeckLibraryUiState>(DeckLibraryUiState.Loading)
    val uiState: StateFlow<DeckLibraryUiState> = _uiState.asStateFlow()

    init {
        observeDecks()
    }

    private fun observeDecks() {
        repository.observeAllDecks()
            .catch { e ->
                _uiState.value = DeckLibraryUiState.Error(
                    e.message ?: "Failed to load decks",
                )
            }
            .onEach { decks ->
                _uiState.value = if (decks.isEmpty()) {
                    DeckLibraryUiState.Empty
                } else {
                    DeckLibraryUiState.Success(decks)
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Bikin deck baru lalu return ID supaya UI bisa navigate ke deck detail.
     * onSuccess callback dipanggil dengan ID baru, supaya navigation dipicu dari UI layer
     * (ViewModel tidak boleh tahu tentang NavController).
     */
    fun createDeck(title: String, description: String = "", onSuccess: (Long) -> Unit) {
        if (title.isBlank()) return  // validation sederhana
        viewModelScope.launch {
            try {
                val newId = repository.createDeck(title.trim(), description.trim())
                onSuccess(newId)
            } catch (e: Exception) {
                _uiState.value = DeckLibraryUiState.Error(
                    e.message ?: "Failed to create deck",
                )
            }
        }
    }

    fun deleteDeck(deckId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteDeck(deckId)
            } catch (e: Exception) {
                _uiState.value = DeckLibraryUiState.Error(
                    e.message ?: "Failed to delete deck",
                )
            }
        }
    }
}