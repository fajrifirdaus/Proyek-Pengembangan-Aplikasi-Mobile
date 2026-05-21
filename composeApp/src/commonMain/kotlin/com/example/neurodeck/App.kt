package com.example.neurodeck

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.neurodeck.presentation.navigation.AppNavHost

@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()

        AppNavHost(navController = navController)
    }
}