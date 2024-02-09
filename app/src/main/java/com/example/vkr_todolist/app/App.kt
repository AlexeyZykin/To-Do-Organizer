package com.example.vkr_todolist.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.vkr_todolist.app.koin.koinModules
import com.example.vkr_todolist.cache.room.db.AppDatabase
import com.example.vkr_todolist.presentation.features.settings.LanguageProvider
import com.example.vkr_todolist.presentation.features.settings.ThemeProvider
import com.example.vkr_todolist.presentation.utils.LanguageHelper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(koinModules)
        }

        val theme = ThemeProvider(this).getThemeFromPreferences()
        AppCompatDelegate.setDefaultNightMode(theme)

        val selectedLanguage = LanguageProvider(this).getLanguageFromPreferences()
        LanguageHelper.setLocale(this, selectedLanguage)
    }
}
