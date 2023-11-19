package com.example.vkr_todolist.ui.settings

import android.app.UiModeManager
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.example.vkr_todolist.R
import java.security.InvalidParameterException

class LanguageProvider(private val context: Context) {

    fun getLanguageFromPreferences(): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(
            context.getString(R.string.language_key),
            context.getString(R.string.default_lang_value)
        ) ?: context.getString(R.string.default_lang_value)
    }
}