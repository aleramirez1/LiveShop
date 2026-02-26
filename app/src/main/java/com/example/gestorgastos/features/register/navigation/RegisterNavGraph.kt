package com.example.liveshop.features.register.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.liveshop.core.navigation.Routes
import com.example.liveshop.features.register.presentation.RegisterScreen

fun NavGraphBuilder.registerNavGraph(navController: NavHostController) {
    composable(Routes.REGISTER) {
        RegisterScreen(
            navController = navController,
            onRegistered = {
                navController.navigate(Routes.LIVESHOP) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        )
    }
}


