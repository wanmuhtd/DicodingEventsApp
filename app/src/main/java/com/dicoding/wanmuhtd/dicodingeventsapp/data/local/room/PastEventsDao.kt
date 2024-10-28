package com.dicoding.wanmuhtd.dicodingeventsapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.PastEventsEntity

@Dao
interface PastEventsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvents(event: List<PastEventsEntity>)

    @Update
    suspend fun update(event: PastEventsEntity)

    @Query("UPDATE past_events SET favorite = :favoriteState WHERE id = :id")
    suspend fun updateEvent(id: Int, favoriteState: Boolean)

    @Query("DELETE FROM past_events WHERE favorite = 0")
    suspend fun deleteAll()

    @Query("SELECT * FROM past_events ORDER BY id DESC")
    fun getPastEvents(): LiveData<List<PastEventsEntity>>

    @Query("SELECT EXISTS(SELECT * FROM past_events WHERE id = :id AND favorite = 1)")
    suspend fun isFavorite(id: Int): Boolean

    @Query("SELECT * FROM past_events WHERE id = :id")
    fun isFavoriteCheck(id: Int): LiveData<PastEventsEntity>
}