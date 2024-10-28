package com.dicoding.wanmuhtd.dicodingeventsapp.ui


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.wanmuhtd.dicodingeventsapp.data.EventsRepository
import com.dicoding.wanmuhtd.dicodingeventsapp.di.Injection
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.active.UpcomingEventViewModel
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.favorite.FavoriteEventViewModel
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.home.HomeViewModel
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.past.PastEventViewModel
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.setting.SettingPreferences
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.setting.SettingViewModel

class ViewModelFactory private constructor(
    private val eventsRepository: EventsRepository,
    private val pref: SettingPreferences,
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpcomingEventViewModel::class.java)) {
            return UpcomingEventViewModel(eventsRepository) as T
        }

        if (modelClass.isAssignableFrom(PastEventViewModel::class.java)) {
            return PastEventViewModel(eventsRepository) as T
        }

        if (modelClass.isAssignableFrom(FavoriteEventViewModel::class.java)) {
            return FavoriteEventViewModel(eventsRepository) as T
        }

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(eventsRepository) as T
        }

        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel(pref) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context, pref: SettingPreferences): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context), pref)
            }.also { instance = it }
    }
}