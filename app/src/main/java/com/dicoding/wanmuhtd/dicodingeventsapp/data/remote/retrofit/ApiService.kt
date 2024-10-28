package com.dicoding.wanmuhtd.dicodingeventsapp.data.remote.retrofit

import com.dicoding.wanmuhtd.dicodingeventsapp.data.remote.response.DetailEventResponse
import com.dicoding.wanmuhtd.dicodingeventsapp.data.remote.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int
    ): EventResponse

    @GET("events/{id}")
    fun getDetailEvent(@Path("id") id: Int): Call<DetailEventResponse>

    @GET("/events")
    suspend fun getNearestEvent(
        @Query("active") active: Int = 1,
        @Query("limit") limit: Int = 1
    ): EventResponse

    @GET("events")
    suspend fun searchEvents(
        @Query("active") active: Int,
        @Query("q") query: String
    ): EventResponse
}