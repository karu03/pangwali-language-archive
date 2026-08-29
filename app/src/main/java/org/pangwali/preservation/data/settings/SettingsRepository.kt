package org.pangwali.preservation.data.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("pangwali_settings", Context.MODE_PRIVATE)
    
    private val _isAutoSaveEnabled = MutableStateFlow(prefs.getBoolean("auto_save", true))
    val isAutoSaveEnabled = _isAutoSaveEnabled.asStateFlow()

    fun setAutoSaveEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("auto_save", enabled).apply()
        _isAutoSaveEnabled.value = enabled
    }
}
