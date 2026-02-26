package com.example.liveshop_par.core.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.liveshop_par.core.di.SessionManager
import com.example.liveshop_par.features.liveshop.presentation.navigation.liveShopGraph
import com.example.liveshop_par.features.login.presentation.navigation.loginGraph
import com.example.liveshop_par.features.register.presentation.navigation.registerGraph
import com.example.liveshop_par.presentation.screens.SplashScreenView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun NavigationWrapper(
    sessionManager: SessionManager
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Splash
    ) {
        composable<Splash> {
            SplashScreenView(navController)
        }
        loginGraph(navController, sessionManager)
        registerGraph(navController, sessionManager)
        liveShopGraph(navController, sessionManager)
        Log.d("NavigationWrapper", "Navigation graphs registered")
    }
}
