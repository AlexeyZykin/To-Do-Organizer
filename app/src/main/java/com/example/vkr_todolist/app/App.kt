package com.example.vkr_todolist.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.vkr_todolist.cache.room.db.AppDatabase
import com.example.vkr_todolist.presentation.features.settings.LanguageProvider
import com.example.vkr_todolist.presentation.features.settings.ThemeProvider
import com.example.vkr_todolist.presentation.utils.LanguageHelper

class App: Application() {
    val database by lazy{ AppDatabase.getDataBase(this)}

    override fun onCreate() {
        super.onCreate()
        val theme = ThemeProvider(this).getThemeFromPreferences()
        AppCompatDelegate.setDefaultNightMode(theme)

        val selectedLanguage = LanguageProvider(this).getLanguageFromPreferences()
        LanguageHelper.setLocale(this, selectedLanguage)
    }
}
