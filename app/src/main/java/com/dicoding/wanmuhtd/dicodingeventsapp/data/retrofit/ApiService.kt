package com.dicoding.wanmuhtd.dicodingeventsapp.data.retrofit

import com.dicoding.wanmuhtd.dicodingeventsapp.data.model.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvents(
        @Query("active") active: Int
    ): Call<EventResponse>

    @GET("events")
    fun searchEvents(
        @Query("active") active: Int,
        @Query("q") query: String
    ): Call<EventResponse>
}