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


    fun createDeck(title: String, description: String = "", onSuccess: (Long) -> Unit) {
        if (title.isBlank()) return  // validation
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


    fun updateDeck(deck: com.example.neurodeck.domain.model.Deck, newTitle: String, newDescription: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            try {
                repository.updateDeck(
                    deck.copy(
                        title = newTitle.trim(),
                        description = newDescription.trim(),
                    ),
                )
            } catch (e: Exception) {
                _uiState.value = DeckLibraryUiState.Error(
                    e.message ?: "Failed to update deck",
                )
            }
        }
    }
}
