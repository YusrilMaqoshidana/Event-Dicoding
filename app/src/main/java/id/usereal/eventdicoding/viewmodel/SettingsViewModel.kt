package id.usereal.eventdicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import id.usereal.eventdicoding.ui.reminder.MyReminderWorker
import id.usereal.eventdicoding.ui.settings.SettingPreferences
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SettingsViewModel(
    private val pref: SettingPreferences,
    private val workManager: WorkManager
) : ViewModel() {

    fun getThemeSettings(): LiveData<Boolean> = pref.getThemeSetting().asLiveData()

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getReminderState(): LiveData<Boolean> = pref.getReminderSetting().asLiveData()

    fun setReminder(isReminderActive: Boolean) {
        viewModelScope.launch {
            pref.setReminderSetting(isReminderActive)
            updateReminderSchedule(isReminderActive)
        }
    }

    private fun updateReminderSchedule(isActive: Boolean) {
        viewModelScope.launch {
            val reminderRequest = PeriodicWorkRequestBuilder<MyReminderWorker>(1, TimeUnit.DAYS)
                .addTag(MyReminderWorker.WORK_NAME)
                .build()

            if (isActive) {
                workManager.enqueueUniquePeriodicWork(
                    MyReminderWorker.WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    reminderRequest
                )
            } else {
                workManager.cancelAllWorkByTag(MyReminderWorker.WORK_NAME)
            }
        }
    }
}
