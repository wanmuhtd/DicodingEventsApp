package com.dicoding.wanmuhtd.dicodingeventsapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.FavoriteEventsEntity
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.PastEventsEntity
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.UpcomingEventsEntity


@Database(
    entities = [UpcomingEventsEntity::class, PastEventsEntity::class, FavoriteEventsEntity::class],
    version = 1
)
abstract class EventRoomDatabase : RoomDatabase() {
    abstract fun upcomingEventsDao(): UpcomingEventsDao
    abstract fun pastEventsDao(): PastEventsDao
    abstract fun favoriteEventsDao(): FavoriteEventsDao

    companion object {
        @Volatile
        private var instance: EventRoomDatabase? = null

        fun getInstance(context: Context): EventRoomDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    EventRoomDatabase::class.java,
                    "Event.db"
                ).build()
            }
    }
}