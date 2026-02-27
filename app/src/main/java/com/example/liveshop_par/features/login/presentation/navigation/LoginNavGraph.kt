package com.example.liveshop_par.features.login.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.liveshop_par.core.navigation.Splash
import com.example.liveshop_par.core.navigation.Login
import com.example.liveshop_par.core.navigation.Register
import com.example.liveshop_par.core.navigation.Marketplace
import com.example.liveshop_par.features.login.presentation.screens.LoginScreenView

fun NavGraphBuilder.loginGraph(
    navController: NavHostController
) {
    composable<Login> {
        LoginScreenView(
            onLoginSuccess = {
                navController.navigate(Marketplace) {
                    popUpTo(Login) { inclusive = true }
                }
            },
            onNavigateToRegister = {
                navController.navigate(Register)
            }
        )
    }
}
