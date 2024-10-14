package com.dicoding.wanmuhtd.dicodingeventsapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.wanmuhtd.dicodingeventsapp.data.model.EventResponse
import com.dicoding.wanmuhtd.dicodingeventsapp.data.model.ListEventsItem
import com.dicoding.wanmuhtd.dicodingeventsapp.data.retrofit.ApiConfig
import com.dicoding.wanmuhtd.dicodingeventsapp.util.SingleEventWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class HomeViewModel : ViewModel() {
    //private val _eventList = MutableLiveData<List<ListEventsItem>>()
    //val eventlist: LiveData<List<ListEventsItem>> = _eventList

    private val _activeEventList = MutableLiveData<List<ListEventsItem>>()
    val activeEventList: LiveData<List<ListEventsItem>> = _activeEventList


    private val _pastEventList = MutableLiveData<List<ListEventsItem>>()
    val pastEventList: LiveData<List<ListEventsItem>> = _pastEventList


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    //private val _searchResults = MutableLiveData<List<ListEventsItem>>()
    //val searchResults: LiveData<List<ListEventsItem>> = _searchResults

    private val _errorMessage = MutableLiveData<SingleEventWrapper<String>>()
    val errorMessage: LiveData<SingleEventWrapper<String>> = _errorMessage

    companion object {
        private const val TAG = "UpcomingEventsViewModel"
    }

    init {
        getActiveEvents()
        getPastEvents()
    }

    private fun getPastEvents() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents(0)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse>,
                response: Response<EventResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _pastEventList.value = response.body()?.listEvents
                    }
                } else {
                    handleError("Failed to retrieve data: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                handleFailure(t)
            }
        })
    }

    private fun getActiveEvents() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents(1)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse>,
                response: Response<EventResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _activeEventList.value = response.body()?.listEvents
                    }
                } else {
                    handleError("Failed to retrieve data: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                handleFailure(t)
            }
        })
    }

    private fun handleError(message: String) {
        Log.d(TAG, message)
        _errorMessage.value =
            SingleEventWrapper(message)
    }

    private fun handleFailure(t: Throwable) {
        Log.e(TAG, "onFailure: ${t.message}")
        val errorMessage = when (t) {
            is SocketTimeoutException -> "Request timeout. Please try again."
            is IOException -> "Failed to connect to server. Please check your internet connection."
            else -> "There is an error. ${t.message}"
        }
        _errorMessage.value = SingleEventWrapper(errorMessage)
    }
}
