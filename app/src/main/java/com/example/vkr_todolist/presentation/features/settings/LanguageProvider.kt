package com.example.vkr_todolist.presentation.features.settings

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.vkr_todolist.R

class LanguageProvider(private val context: Context) {

    fun getLanguageFromPreferences(): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(
            context.getString(R.string.language_key),
            context.getString(R.string.default_lang_value)
        ) ?: context.getString(R.string.default_lang_value)
    }
}