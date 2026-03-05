package com.example.farmer.customer.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class ThemeManager(context: Context?) {

    private val dataStore = context!!.dataStore
    private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")

    val isDarkMode: Flow<Boolean?> = dataStore.data.map { it[IS_DARK_MODE] }

    suspend fun setDarkMode(isDark: Boolean) {
        dataStore.edit { it[IS_DARK_MODE] = isDark }
    }
}