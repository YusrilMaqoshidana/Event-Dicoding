package id.usereal.eventdicoding.ui.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val themeKey = booleanPreferencesKey("theme_setting")
    private val reminderKey = booleanPreferencesKey("reminder_setting")
    fun getThemeSetting(): Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[themeKey] ?: false }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[themeKey] = isDarkModeActive
        }
    }

    fun getReminderSetting(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[reminderKey] ?: false
    }

    suspend fun setReminderSetting(isReminderActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[reminderKey] = isReminderActive
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingPreferences(dataStore).also { INSTANCE = it }
            }
    }
}