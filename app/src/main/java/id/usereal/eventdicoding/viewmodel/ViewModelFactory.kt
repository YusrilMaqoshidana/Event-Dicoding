import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.usereal.eventdicoding.data.remote.retrofit.ApiConfig
import id.usereal.eventdicoding.di.Injection
import id.usereal.eventdicoding.ui.settings.SettingPreferences
import id.usereal.eventdicoding.viewmodel.DetailEventViewModel
import id.usereal.eventdicoding.viewmodel.EventViewModel

object ViewModelFactory {
    fun getInstance(context: Context) = viewModelFactory {
        val repository = Injection.provideRepository(context)
        val pref = Injection.provideSettingPreferences(context)
        initializer {
            EventViewModel(repository)
        }
        initializer {
            DetailEventViewModel(repository)
        }
        initializer {
            FavoriteViewModel(repository)
        }
        initializer {
            SettingsViewModel(pref)
        }
    }
}
