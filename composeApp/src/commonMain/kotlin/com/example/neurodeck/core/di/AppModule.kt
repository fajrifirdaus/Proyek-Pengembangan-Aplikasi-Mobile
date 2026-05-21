package com.example.neurodeck.core.di

import com.example.neurodeck.core.network.HttpClientFactory
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import com.example.neurodeck.data.repository.DeckRepositoryImpl
import com.example.neurodeck.domain.repository.DeckRepository
import com.example.neurodeck.data.repository.CardRepositoryImpl
import com.example.neurodeck.domain.repository.CardRepository
import com.example.neurodeck.presentation.screens.decklibrary.DeckLibraryViewModel
import com.example.neurodeck.presentation.screens.studysession.StudySessionViewModel
import org.koin.core.module.dsl.viewModel

// ==================== NETWORK MODULE ====================

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
}

// ==================== DATABASE MODULE ====================
// TODO Sprint 2: register NeuroDeckDatabase here after SQLDelight schema added

val databaseModule = module {
    // empty for now
}

// ==================== REPOSITORY MODULE ====================

val repositoryModule = module {
    single<DeckRepository> { DeckRepositoryImpl(get()) }
    single<CardRepository> { CardRepositoryImpl(get(), get()) }
}

// ==================== USE CASE MODULE ====================
// TODO Sprint 2: register flashcard use cases here

val useCaseModule = module {
    // empty for now
}

// ==================== VIEWMODEL MODULE ====================
// TODO Sprint 2: register ViewModels here

val viewModelModule = module {
    viewModel { DeckLibraryViewModel(get()) }
    viewModel { params ->
        StudySessionViewModel(
            deckId = params.get(),
            cardRepository = get(),
        )
    }
    // TODO D.5: register lebih banyak ViewModel (AddCard, Statistics, dll)
}

// ==================== SHARED MODULES ====================

val sharedModules = listOf(
    networkModule,
    databaseModule,
    repositoryModule,
    useCaseModule,
    viewModelModule,
)

// ==================== INIT FUNCTION ====================

fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null,
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}