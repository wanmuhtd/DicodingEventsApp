package com.dicoding.wanmuhtd.dicodingeventsapp.ui.setting

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.DailyReminderWorker
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SettingViewModel(private val pref: SettingPreferences) : ViewModel() {
    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getDailyReminderSetting(): LiveData<Boolean> {
        return pref.getDailyReminderSetting().asLiveData()
    }

    fun saveDailyReminderSetting(isEnabled: Boolean) {
        viewModelScope.launch {
            pref.saveDailyReminderSetting(isEnabled)
        }
    }

    fun scheduleDailyReminder(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val reminder =
            PeriodicWorkRequest.Builder(
                DailyReminderWorker::class.java,
                24, TimeUnit.HOURS
            ).setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
                .build()
        workManager.enqueueUniquePeriodicWork(
            "DailyReminderWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            reminder
        )
    }

    fun cancelDailyReminder(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork("DailyReminder")
    }

    private fun calculateInitialDelay(): Long {
        val now = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 39)
            set(Calendar.SECOND, 0)
        }

        if (now.after(targetTime)) {
            targetTime.add(
                Calendar.DAY_OF_YEAR,
                1
            )
        }

        return targetTime.timeInMillis - now.timeInMillis
    }
}