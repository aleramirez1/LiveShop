package com.example.liveshop.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.liveshop.features.auth.navigation.authNavGraph
import com.example.liveshop.features.liveshop.navigation.liveshopNavGraph
import com.example.liveshop.features.register.navigation.registerNavGraph

@Composable
fun AppNavHost(
    startDestination: String,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        authNavGraph(navController)
        registerNavGraph(navController)
        liveshopNavGraph()
    }
}

