package com.dicoding.wanmuhtd.dicodingeventsapp.ui.active

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.wanmuhtd.dicodingeventsapp.data.EventsRepository
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.UpcomingEventsEntity
import kotlinx.coroutines.launch


class UpcomingEventViewModel(private val eventsRepository: EventsRepository) : ViewModel() {


    fun searchEvents(query: String) = eventsRepository.searchUpcomingEvents(query)

    fun getUpcomingEvents() = eventsRepository.getUpcomingEvents()

    fun checkIsFavorite(id: Int): LiveData<UpcomingEventsEntity> {
        return eventsRepository.checkIsFavoriteUpcoming(id)
    }
    fun saveEvent(id: Int) {
        viewModelScope.launch {
            eventsRepository.setUpcomingFavorite(id, true)
        }
    }

    fun deleteEvent(id: Int) {
        viewModelScope.launch {
            eventsRepository.setUpcomingFavorite(id, false)
        }
    }
}
