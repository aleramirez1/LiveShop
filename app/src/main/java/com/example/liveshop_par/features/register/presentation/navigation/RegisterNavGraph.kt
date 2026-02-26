package com.example.liveshop_par.features.register.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.liveshop_par.core.di.SessionManager
import com.example.liveshop_par.core.navigation.LiveShop
import com.example.liveshop_par.core.navigation.Register
import com.example.liveshop_par.features.register.presentation.screens.RegisterScreenView

fun NavGraphBuilder.registerGraph(
    navController: NavHostController,
    sessionManager: SessionManager
) {
    composable<Register> {
        RegisterScreenView(
            onRegisterSuccess = { userId, userName, userEmail ->
                sessionManager.setSession(userId, userName, userEmail)
                navController.navigate(LiveShop) {
                    popUpTo(Register) { inclusive = true }
                }
            },
            onNavigateToLogin = {
                navController.popBackStack()
            }
        )
    }
}
