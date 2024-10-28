package com.dicoding.wanmuhtd.dicodingeventsapp.ui.past


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.wanmuhtd.dicodingeventsapp.data.EventsRepository
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.PastEventsEntity
import kotlinx.coroutines.launch

class PastEventViewModel(private val eventsRepository: EventsRepository) : ViewModel() {


    fun searchEvents(query: String) = eventsRepository.searchPastEvents(query)

    fun getPastEvents() = eventsRepository.getPastEvents()

    fun checkIsFavorite(id: Int): LiveData<PastEventsEntity> {
        return eventsRepository.checkIsFavoritePast(id)
    }

    fun saveEvent(id: Int) {
        viewModelScope.launch {
            eventsRepository.setPastFavorite(id, true)
        }
    }

    fun deleteEvent(id: Int) {
        viewModelScope.launch {
            eventsRepository.setPastFavorite(id, false)
        }
    }
}
