package com.example.vkr_todolist.presentation.features.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.vkr_todolist.R
import com.example.vkr_todolist.presentation.utils.LanguageHelper


class SettingsFragment : PreferenceFragmentCompat() {
    private val themeProvider by lazy { ThemeProvider(requireContext()) }
    private val themePreference by lazy {
        findPreference<ListPreference>(getString(R.string.theme_key))
    }

    private val languageProvider by lazy { LanguageProvider(requireContext()) }
    private val languagePreference by lazy {
        findPreference<ListPreference>(getString(R.string.language_key))
    }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
        setThemePreference()
        setLanguagePreference()
    }


    private fun setThemePreference(){
        themePreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                if (newValue is String) {
                    val theme = themeProvider.getTheme(newValue)
                    AppCompatDelegate.setDefaultNightMode(theme)
                }
                true
            }
    }


    private fun setLanguagePreference() {
        val supportedLanguages = resources.getStringArray(R.array.app_language_array)
        val supportedLanguageCodes = resources.getStringArray(R.array.app_language_values)
        languagePreference?.entries = supportedLanguages
        languagePreference?.entryValues = supportedLanguageCodes


        languagePreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                if (newValue is String) {
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    sharedPreferences.edit().putString(getString(R.string.language_key), newValue).apply()
                    LanguageHelper.setLocale(requireContext(), newValue)
                    activity?.recreate()
                }
                true
                }
    }



    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}