package com.example.neurodeck.presentation.screens.cardlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.model.Card
import com.example.neurodeck.domain.model.Deck
import com.example.neurodeck.domain.repository.CardRepository
import com.example.neurodeck.domain.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * State untuk CardList screen.
 *
 * Loading: data sedang di-fetch (saat pertama buka screen)
 * Success: punya data deck + cards (bisa kosong, lihat cards.isEmpty())
 * Error: gagal load (deck tidak ditemukan, dll)
 */
sealed interface CardListUiState {
    data object Loading : CardListUiState
    data class Success(
        val deck: Deck,
        val cards: List<Card>,
    ) : CardListUiState
    data class Error(val message: String) : CardListUiState
}

/**
 * ViewModel untuk CardListScreen.
 *
 * Menggabungkan 2 Flow: observeDeckById + observeCardsByDeck → 1 UI state.
 * Pakai `combine` operator: emit ulang setiap kali salah satu source berubah.
 */
class CardListViewModel(
    private val deckId: Long,
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CardListUiState>(CardListUiState.Loading)
    val uiState: StateFlow<CardListUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        combine(
            deckRepository.observeDeckById(deckId),
            cardRepository.observeCardsByDeck(deckId),
        ) { deck, cards ->
            if (deck == null) {
                CardListUiState.Error("Deck tidak ditemukan (mungkin sudah dihapus)")
            } else {
                CardListUiState.Success(deck = deck, cards = cards)
            }
        }
            .catch { e ->
                _uiState.value = CardListUiState.Error(
                    e.message ?: "Gagal load data deck",
                )
            }
            .onEach { state -> _uiState.value = state }
            .launchIn(viewModelScope)
    }

    fun deleteCard(cardId: Long) {
        viewModelScope.launch {
            try {
                cardRepository.deleteCard(cardId)
            } catch (e: Exception) {
                _uiState.value = CardListUiState.Error(
                    e.message ?: "Gagal hapus kartu",
                )
            }
        }
    }
}