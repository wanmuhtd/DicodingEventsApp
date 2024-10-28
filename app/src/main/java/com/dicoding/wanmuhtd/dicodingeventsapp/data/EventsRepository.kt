package com.dicoding.wanmuhtd.dicodingeventsapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.FavoriteEventsEntity
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.PastEventsEntity
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.UpcomingEventsEntity
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.room.FavoriteEventsDao
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.room.UpcomingEventsDao
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.room.PastEventsDao
import com.dicoding.wanmuhtd.dicodingeventsapp.data.remote.response.EventResponse
import com.dicoding.wanmuhtd.dicodingeventsapp.data.remote.retrofit.ApiService

class EventsRepository private constructor(
    private val apiService: ApiService,
    private val upcomingEventsDao: UpcomingEventsDao,
    private val pastEventsDao: PastEventsDao,
    private val favoriteEventsDao: FavoriteEventsDao
) {
    fun getUpcomingEvents(): LiveData<Result<List<UpcomingEventsEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response: EventResponse = apiService.getEvents(1)
            val events = response.listEvents
            val upcomingEventsList = events.map { event ->
                val isFavorite = upcomingEventsDao.isFavorite(event.id)
                UpcomingEventsEntity(
                    event.id,
                    event.summary,
                    event.mediaCover,
                    event.registrants,
                    event.imageLogo,
                    event.link,
                    event.description,
                    event.ownerName,
                    event.cityName,
                    event.quota,
                    event.name,
                    event.beginTime,
                    event.endTime,
                    event.category,
                    isFavorite
                )
            }
            upcomingEventsDao.deleteAll()
            upcomingEventsDao.insertEvents(upcomingEventsList)
        } catch (e: Exception) {
            Log.d("EventsRepository", "getUpcomingEvents: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<UpcomingEventsEntity>>> =
            upcomingEventsDao.getUpcomingEvents().map { Result.Success(it) }
        emitSource(localData)
    }

    fun getPastEvents(): LiveData<Result<List<PastEventsEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response: EventResponse = apiService.getEvents(0)
            val events = response.listEvents
            val pastEventsList = events.map { event ->
                val isFavorite = pastEventsDao.isFavorite(event.id)
                PastEventsEntity(
                    event.id,
                    event.summary,
                    event.mediaCover,
                    event.registrants,
                    event.imageLogo,
                    event.link,
                    event.description,
                    event.ownerName,
                    event.cityName,
                    event.quota,
                    event.name,
                    event.beginTime,
                    event.endTime,
                    event.category,
                    isFavorite
                )
            }
            pastEventsDao.deleteAll()
            pastEventsDao.insertEvents(pastEventsList)
        } catch (e: Exception) {
            Log.d("EventsRepository", "getPastEvents: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<PastEventsEntity>>> =
            pastEventsDao.getPastEvents().map { Result.Success(it) }
        emitSource(localData)
    }

    fun searchUpcomingEvents(query: String): LiveData<Result<List<UpcomingEventsEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response: EventResponse = apiService.searchEvents(1, query)
            val events = response.listEvents
            val searchUpcomingEventsList = events.map { event ->
                val isFavorite = upcomingEventsDao.isFavorite(event.id)
                UpcomingEventsEntity(
                    event.id,
                    event.summary,
                    event.mediaCover,
                    event.registrants,
                    event.imageLogo,
                    event.link,
                    event.description,
                    event.ownerName,
                    event.cityName,
                    event.quota,
                    event.name,
                    event.beginTime,
                    event.endTime,
                    event.category,
                    isFavorite
                )
            }
            upcomingEventsDao.deleteAll()
            upcomingEventsDao.insertEvents(searchUpcomingEventsList)
        } catch (e: Exception) {
            Log.d("EventsRepository", "getSearchUpcomingEvents: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<UpcomingEventsEntity>>> =
            upcomingEventsDao.getUpcomingEvents().map { events ->
                Result.Success(events.filter { it.name?.contains(query, ignoreCase = true) ?: false })
            }
        emitSource(localData)
    }

    fun searchPastEvents(query: String): LiveData<Result<List<PastEventsEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response: EventResponse = apiService.searchEvents(0, query)
            val events = response.listEvents
            val searchPastEventsList = events.map { event ->
                val isFavorite = pastEventsDao.isFavorite(event.id)
                PastEventsEntity(
                    event.id,
                    event.summary,
                    event.mediaCover,
                    event.registrants,
                    event.imageLogo,
                    event.link,
                    event.description,
                    event.ownerName,
                    event.cityName,
                    event.quota,
                    event.name,
                    event.beginTime,
                    event.endTime,
                    event.category,
                    isFavorite
                )
            }
            pastEventsDao.deleteAll()
            pastEventsDao.insertEvents(searchPastEventsList)
        } catch (e: Exception) {
            Log.d("EventsRepository", "getSearchPastEvents: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<PastEventsEntity>>> =
            pastEventsDao.getPastEvents().map { events ->
                Result.Success(events.filter { it.name?.contains(query, ignoreCase = true) ?: false})
            }
        emitSource(localData)
    }

    fun searchFavoriteEvents(query: String): LiveData<Result<List<FavoriteEventsEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val localData: LiveData<Result<List<FavoriteEventsEntity>>> = favoriteEventsDao.searchFavoriteEvents(query).map { events ->
                Result.Success(events.filter { it.name?.contains(query, ignoreCase = true) ?: false })
            }
            emitSource(localData)
        } catch (e: Exception) {
            Log.d("EventsRepository", "searchFavoriteEvents: ${e.message}")
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun insertFavoriteEvent(favoriteEvent: FavoriteEventsEntity){
        favoriteEventsDao.insertFavoriteEvent(favoriteEvent)
    }

    suspend fun deleteFavoriteEvent(id: Int){
        favoriteEventsDao.deleteFaforite(id)
    }

    suspend fun setUpcomingFavorite(id: Int, favoriteState: Boolean){
        upcomingEventsDao.updateEvent(id, favoriteState)
    }

    suspend fun setPastFavorite(id: Int, favoriteState: Boolean){
        pastEventsDao.updateEvent(id, favoriteState)
    }

    fun checkIsFavoriteUpcoming(id: Int): LiveData<UpcomingEventsEntity> {
        return upcomingEventsDao.isFavoriteCheck(id)
    }

    fun checkIsFavoritePast(id: Int): LiveData<PastEventsEntity> {
        return pastEventsDao.isFavoriteCheck(id)
    }

    fun getFavoriteEvents(): LiveData<List<FavoriteEventsEntity>> {
        return favoriteEventsDao.getFavoriteEvents()
    }

    companion object{
        @Volatile
        private var instance: EventsRepository? = null
        fun getInstance(
            apiService: ApiService,
            upcomingEventsDao: UpcomingEventsDao,
            pastEventsDao: PastEventsDao,
            favoriteEventsDao: FavoriteEventsDao
        ): EventsRepository =
            instance ?: synchronized(this){
                instance ?: EventsRepository(apiService, upcomingEventsDao, pastEventsDao, favoriteEventsDao)
            }.also { instance = it }
    }
}