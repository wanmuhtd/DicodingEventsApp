package com.dicoding.wanmuhtd.dicodingeventsapp.di

import android.content.Context
import com.dicoding.wanmuhtd.dicodingeventsapp.data.EventsRepository
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.room.EventRoomDatabase
import com.dicoding.wanmuhtd.dicodingeventsapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventsRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventRoomDatabase.getInstance(context)
        val upcomingEventsDao = database.upcomingEventsDao()
        val pastEventsDao = database.pastEventsDao()
        val favoriteEventsDao = database.favoriteEventsDao()
        return EventsRepository.getInstance(apiService, upcomingEventsDao, pastEventsDao, favoriteEventsDao)
    }
}