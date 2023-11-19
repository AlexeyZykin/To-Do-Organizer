package com.example.vkr_todolist.utils

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import java.util.*

object LanguageHelper {

    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

}