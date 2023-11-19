package com.example.vkr_todolist

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationManagerCompat
import com.example.vkr_todolist.data.local.AppDatabase
import com.example.vkr_todolist.ui.main.MainActivity
import com.example.vkr_todolist.ui.settings.LanguageProvider
import com.example.vkr_todolist.ui.settings.ThemeProvider
import com.example.vkr_todolist.utils.LanguageHelper
import com.example.vkr_todolist.utils.NotificationHelper

class App: Application() {
    val database by lazy{AppDatabase.getDataBase(this)}

    override fun onCreate() {
        super.onCreate()
        val theme = ThemeProvider(this).getThemeFromPreferences()
        AppCompatDelegate.setDefaultNightMode(theme)

        val selectedLanguage = LanguageProvider(this).getLanguageFromPreferences()
     //   Log.d("TAG", "lang ${selectedLanguage}")
        LanguageHelper.setLocale(this, selectedLanguage)
    }
}
