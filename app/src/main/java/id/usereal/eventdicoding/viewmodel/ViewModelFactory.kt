package id.usereal.eventdicoding.viewmodel

import android.content.Context
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.WorkManager
import id.usereal.eventdicoding.di.Injection

object ViewModelFactory {
    fun getInstance(context: Context) = viewModelFactory {
        val repository = Injection.provideRepository(context)
        val pref = Injection.provideSettingPreferences(context)
        val workManager = WorkManager.getInstance(context)

        initializer {
            EventViewModel(repository)
        }

        initializer {
            FavoriteViewModel(repository)
        }

        initializer {
            DetailEventViewModel(repository)
        }

        initializer {
            SettingsViewModel(pref, workManager)
        }
    }
}
