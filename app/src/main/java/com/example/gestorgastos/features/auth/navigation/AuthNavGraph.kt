package com.example.liveshop.features.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.liveshop.core.navigation.Routes
import com.example.liveshop.features.auth.presentation.LoginScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    composable(Routes.LOGIN) {
        LoginScreen(
            navController = navController,
            onLoggedIn = {
                navController.navigate(Routes.LIVESHOP) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        )
    }
}

