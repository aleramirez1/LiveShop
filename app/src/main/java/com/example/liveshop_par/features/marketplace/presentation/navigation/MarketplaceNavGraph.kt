package com.example.liveshop_par.features.marketplace.presentation.navigation

import android.net.Uri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.liveshop_par.core.navigation.Login
import com.example.liveshop_par.core.navigation.Marketplace
import com.example.liveshop_par.features.marketplace.presentation.screens.MarketplaceScreenView
import com.example.liveshop_par.features.marketplace.presentation.viewmodels.MarketplaceViewModelImpl

fun NavGraphBuilder.marketplaceGraph(
    navController: NavHostController
) {
    composable<Marketplace> {
        val viewModel: MarketplaceViewModelImpl = hiltViewModel()
        
        MarketplaceScreenView(
            viewModel = viewModel,
            onLogout = {
                navController.navigate(Login) {
                    popUpTo(Marketplace) { inclusive = true }
                }
            }
        )
    }
}
