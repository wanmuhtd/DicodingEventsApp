package com.dicoding.wanmuhtd.dicodingeventsapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.FavoriteEventsEntity

@Dao
interface FavoriteEventsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteEvent(event: FavoriteEventsEntity)

    @Query("DELETE FROM favorite_events WHERE id = :id")
    suspend fun deleteFaforite(id: Int)

    @Query("SELECT * FROM favorite_events ORDER BY id DESC")
    fun getFavoriteEvents(): LiveData<List<FavoriteEventsEntity>>

    @Query("SELECT * FROM favorite_events WHERE name LIKE '%' || :query || '%'")
    fun searchFavoriteEvents(query: String): LiveData<List<FavoriteEventsEntity>>
}