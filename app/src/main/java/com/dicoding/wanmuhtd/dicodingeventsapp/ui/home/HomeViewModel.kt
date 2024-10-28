package com.dicoding.wanmuhtd.dicodingeventsapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.wanmuhtd.dicodingeventsapp.data.EventsRepository
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.PastEventsEntity
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.UpcomingEventsEntity
import kotlinx.coroutines.launch

@Suppress("unused")
class HomeViewModel(private val eventsRepository: EventsRepository) : ViewModel() {


    fun searchEvents(query: String) = eventsRepository.searchUpcomingEvents(query)

    fun getUpcomingEvents() = eventsRepository.getUpcomingEvents()
    fun getPastEvents() = eventsRepository.getPastEvents()

    fun checkIsFavoriteUpcoming(id: Int): LiveData<UpcomingEventsEntity> {
        return eventsRepository.checkIsFavoriteUpcoming(id)
    }

    fun saveEventUpcoming(id: Int) {
        viewModelScope.launch {
            eventsRepository.setUpcomingFavorite(id, true)
        }
    }

    fun deleteEventUpcoming(id: Int) {
        viewModelScope.launch {
            eventsRepository.setUpcomingFavorite(id, false)
        }
    }

    fun checkIsFavoritePast(id: Int): LiveData<PastEventsEntity> {
        return eventsRepository.checkIsFavoritePast(id)
    }

    fun saveEventPast(id: Int) {
        viewModelScope.launch {
            eventsRepository.setPastFavorite(id, true)
        }
    }

    fun deleteEventPast(id: Int) {
        viewModelScope.launch {
            eventsRepository.setPastFavorite(id, false)
        }
    }
}

