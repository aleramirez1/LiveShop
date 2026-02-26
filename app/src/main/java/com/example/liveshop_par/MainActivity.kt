package com.example.liveshop_par

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.liveshop_par.core.navigation.NavigationWrapper
import com.example.liveshop_par.ui.theme.LiveShop_parTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LiveShop_parTheme {
                NavigationWrapper()
            }
        }
    }
}
