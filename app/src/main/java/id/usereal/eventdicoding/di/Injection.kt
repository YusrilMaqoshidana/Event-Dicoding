package id.usereal.eventdicoding.di

import id.usereal.eventdicoding.ui.settings.SettingPreferences
import android.content.Context
import id.usereal.eventdicoding.data.local.room.FavoriteRoomDatabase
import id.usereal.eventdicoding.data.remote.retrofit.ApiConfig
import id.usereal.eventdicoding.data.repository.EventRepository
import id.usereal.eventdicoding.ui.settings.dataStore

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = FavoriteRoomDatabase.getDatabase(context)
        val dao = database.eventDao()
        return EventRepository.getInstance(apiService, dao)
    }

    fun provideSettingPreferences(context: Context): SettingPreferences {
        return SettingPreferences.getInstance(context.dataStore)
    }
}