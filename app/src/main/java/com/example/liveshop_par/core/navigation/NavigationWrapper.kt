package com.example.liveshop_par.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.liveshop_par.features.login.presentation.navigation.loginGraph
import com.example.liveshop_par.features.register.presentation.navigation.registerGraph
import com.example.liveshop_par.features.marketplace.presentation.navigation.marketplaceGraph
import com.example.liveshop_par.presentation.screens.SplashScreenView

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Splash
    ) {
        composable<Splash> {
            SplashScreenView(navController)
        }
        loginGraph(navController)
        registerGraph(navController)
        marketplaceGraph(navController)
    }
}
