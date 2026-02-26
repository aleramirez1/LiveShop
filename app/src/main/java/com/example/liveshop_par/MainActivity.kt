package com.example.liveshop_par

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.liveshop_par.core.di.SessionManager
import com.example.liveshop_par.core.navigation.NavigationWrapper
import com.example.liveshop_par.ui.theme.LiveShop_parTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LiveShop_parTheme {
                NavigationWrapper(sessionManager)
            }
        }
    }
}
