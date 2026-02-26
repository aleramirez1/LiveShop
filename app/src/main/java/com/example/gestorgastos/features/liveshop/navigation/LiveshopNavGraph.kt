package com.example.liveshop.features.liveshop.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.liveshop.core.navigation.Routes
import com.example.liveshop.features.liveshop.presentation.screens.LiveshopScreen

fun NavGraphBuilder.liveshopNavGraph() {
    composable(Routes.LIVESHOP) {
        LiveshopScreen()
    }
}
