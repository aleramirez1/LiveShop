package com.example.liveshop_par

import android.app.Application
import com.example.liveshop_par.core.di.SessionManager
import com.example.liveshop_par.data.network.ApiClient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LiveShopApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val sessionManager = SessionManager()
        ApiClient.setSessionManager(sessionManager)
    }
}