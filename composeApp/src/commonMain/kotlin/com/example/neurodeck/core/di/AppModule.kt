package com.example.neurodeck.core.di

import com.example.neurodeck.core.network.HttpClientFactory
import com.example.neurodeck.core.util.DatabaseDriverFactory
import com.example.neurodeck.data.local.NeuroDeckDatabase
import com.example.neurodeck.data.repository.CardRepositoryImpl
import com.example.neurodeck.data.repository.DeckRepositoryImpl
import com.example.neurodeck.domain.repository.CardRepository
import com.example.neurodeck.domain.repository.DeckRepository
import com.example.neurodeck.domain.usecase.CalculateNextReviewUseCase
import com.example.neurodeck.presentation.screens.decklibrary.DeckLibraryViewModel
import com.example.neurodeck.presentation.screens.studysession.StudySessionViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import com.example.neurodeck.presentation.screens.cardlist.CardListViewModel
import com.example.neurodeck.presentation.screens.addcard.AddCardViewModel
import com.example.neurodeck.presentation.screens.editcard.EditCardViewModel
import com.example.neurodeck.data.remote.api.GeminiService
import com.example.neurodeck.data.repository.AIRepositoryImpl
import com.example.neurodeck.domain.repository.AIRepository

// ==================== NETWORK MODULE ====================

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    single {
        GeminiService(
            httpClient = get(),
            apiKey = getApiKey(),
        )
    }
}

// ==================== DATABASE MODULE ====================
// NeuroDeckDatabase di-instantiate sekali (singleton) dengan SqlDriver dari
// DatabaseDriverFactory yang di-provide platform-specific module (androidModule/iosModule).

val databaseModule = module {
    single { NeuroDeckDatabase(get<DatabaseDriverFactory>().createDriver()) }
}

// ==================== REPOSITORY MODULE ====================

val repositoryModule = module {
    single<DeckRepository> { DeckRepositoryImpl(get()) }
    single<CardRepository> { CardRepositoryImpl(get(), get()) }
    single<AIRepository> { AIRepositoryImpl(get()) }
}

// ==================== USE CASE MODULE ====================
// Use case di-register sebagai single karena stateless dan reusable.

val useCaseModule = module {
    single { CalculateNextReviewUseCase() }
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModel { DeckLibraryViewModel(get()) }
    viewModel { params ->
        StudySessionViewModel(
            deckId = params.get(),
            cardRepository = get(),
        )
    }
    viewModel { params ->
        CardListViewModel(
            deckId = params.get(),
            deckRepository = get(),
            cardRepository = get(),
        )
    }
    viewModel { params ->
        AddCardViewModel(
            deckId = params.get(),
            cardRepository = get(),
        )
    }

    viewModel { params ->
        EditCardViewModel(
            cardId = params.get(),
            cardRepository = get(),
        )
    }
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
// ==================== PLATFORM-SPECIFIC: API KEY ====================
// API key di-load berbeda per platform:
// - Android: dari BuildConfig (di-inject saat build via local.properties)
// - iOS    : hardcoded (placeholder, akan di-implement nanti)

expect fun getApiKey(): String
