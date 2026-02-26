package com.example.liveshop_par.features.liveshop.presentation.navigation

import androidx.compose.runtime.collectAsState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.liveshop_par.core.di.SessionManager
import com.example.liveshop_par.core.navigation.LiveShop
import com.example.liveshop_par.core.navigation.Login
import com.example.liveshop_par.features.liveshop.presentation.screens.LiveShopScreenView

fun NavGraphBuilder.liveShopGraph(
    navController: NavHostController,
    sessionManager: SessionManager
) {
    composable<LiveShop> {
        val userId = sessionManager.userId.collectAsState().value ?: 0
        LiveShopScreenView(
            onLogout = {
                sessionManager.clearSession()
                navController.navigate(Login) {
                    popUpTo(LiveShop) { inclusive = true }
                }
            },
            userId = userId
        )
    }
}
