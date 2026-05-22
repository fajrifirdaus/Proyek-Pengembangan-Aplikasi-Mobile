package com.example.neurodeck

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.neurodeck.presentation.navigation.AppNavHost
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        MaterialTheme {
            val navController = rememberNavController()

            AppNavHost(navController = navController)
        }
    }
}