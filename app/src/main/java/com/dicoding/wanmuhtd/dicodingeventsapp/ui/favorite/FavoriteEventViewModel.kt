package com.dicoding.wanmuhtd.dicodingeventsapp.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.wanmuhtd.dicodingeventsapp.data.EventsRepository
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.FavoriteEventsEntity
import com.dicoding.wanmuhtd.dicodingeventsapp.util.SingleEventWrapper
import kotlinx.coroutines.launch

@Suppress("unused")
class FavoriteEventViewModel(private val eventsRepository: EventsRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<SingleEventWrapper<String>>()
    val errorMessage: LiveData<SingleEventWrapper<String>> = _errorMessage

    fun searchEvents(query: String) = eventsRepository.searchFavoriteEvents(query)

    fun getFavoriteEvents() = eventsRepository.getFavoriteEvents()

    fun insertFavoriteEvent(favoriteEventsEntity: FavoriteEventsEntity) {
        viewModelScope.launch {
            eventsRepository.insertFavoriteEvent(favoriteEventsEntity)
        }
    }

    fun deleteFavoriteEvent(id: Int) {
        viewModelScope.launch {
            eventsRepository.deleteFavoriteEvent(id)
        }
    }
}