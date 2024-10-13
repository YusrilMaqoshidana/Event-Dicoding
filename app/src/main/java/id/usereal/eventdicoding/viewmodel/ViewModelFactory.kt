import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.usereal.eventdicoding.ui.settings.SettingPreferences

class ViewModelFactory private constructor(
    private val settingPreferences: SettingPreferences
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(settingPreferences) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(pref: SettingPreferences): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    pref
                ).also { instance = it }
            }
    }
}
