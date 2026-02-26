package com.example.liveshop_par.features.login.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.liveshop_par.core.di.SessionManager
import com.example.liveshop_par.core.navigation.Login
import com.example.liveshop_par.core.navigation.Register
import com.example.liveshop_par.features.login.presentation.screens.LoginScreenView

fun NavGraphBuilder.loginGraph(
    navController: NavHostController,
    sessionManager: SessionManager
) {
    composable<Login> {
        LoginScreenView(
            onLoginSuccess = { userId, userName, userEmail ->
                sessionManager.setSession(userId, userName, userEmail)
                navController.navigate(com.example.liveshop_par.core.navigation.LiveShop) {
                    popUpTo(Login) { inclusive = true }
                }
            },
            onNavigateToRegister = {
                navController.navigate(Register)
            }
        )
    }
}
