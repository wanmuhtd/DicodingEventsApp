package com.dicoding.wanmuhtd.dicodingeventsapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.wanmuhtd.dicodingeventsapp.data.model.DetailEventResponse
import com.dicoding.wanmuhtd.dicodingeventsapp.data.model.Event
import com.dicoding.wanmuhtd.dicodingeventsapp.data.retrofit.ApiConfig
import com.dicoding.wanmuhtd.dicodingeventsapp.util.SingleEventWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class DetailViewModel(private val eventId: Int) : ViewModel() {
    private val _event = MutableLiveData<Event?>()
    val event: LiveData<Event?> = _event

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<SingleEventWrapper<String>>()
    val errorMessage: LiveData<SingleEventWrapper<String>> = _errorMessage

    companion object {
        private const val TAG = "PastEventsViewModel"
    }

    init {
        getDetailEvent(eventId)
    }

    private fun getDetailEvent(eventId: Int) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailEvent(eventId)
        client.enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(
                call: Call<DetailEventResponse>,
                response: Response<DetailEventResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _event.value = response.body()?.event
                    }
                } else {
                    handleError("Failed to retrieve data: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
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