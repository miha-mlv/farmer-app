package com.example.farmer.customer.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.farmer.customer.data.ThemeManager
import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("e793a345-d9df-4603-8bc9-1a28bfc5d024")
        MapKitFactory.initialize(this)

        val themeManager = ThemeManager(this)
        CoroutineScope(Dispatchers.Main).launch {
            themeManager.isDarkMode.first()?.let { isDark ->
                val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }
    }
}