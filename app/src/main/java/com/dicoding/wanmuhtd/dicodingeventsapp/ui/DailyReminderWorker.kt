package com.dicoding.wanmuhtd.dicodingeventsapp.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dicoding.wanmuhtd.dicodingeventsapp.MainActivity
import com.dicoding.wanmuhtd.dicodingeventsapp.R
import com.dicoding.wanmuhtd.dicodingeventsapp.data.remote.retrofit.ApiConfig
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.setting.dataStore
import kotlinx.coroutines.flow.first


@Suppress("UNUSED_VARIABLE", "unused")
class DailyReminderWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val REMINDER_KEY = "reminder_key"
        const val CHANNEL_NAME = "Daily Reminder"
        const val CHANNEL_ID = "reminder_channel"
        const val NOTIFICATION_ID = 1
    }

    override suspend fun doWork(): Result {

        val isReminderActive = applicationContext.dataStore.data.first()[booleanPreferencesKey(
            REMINDER_KEY
        )] ?: false

        if (isReminderActive) {
            try {
                val apiService = ApiConfig.getApiService()
                val response = apiService.getNearestEvent()
                Log.d("DailyReminderWorker", "Response: $response")
                if (!response.error!! && response.listEvents.isNotEmpty()) {
                    val event = response.listEvents[0]
                    Log.d("DailyReminderWorker", "Event: $event")
                    showNotification(event.name, event.beginTime)
                } else {
                    Log.d("DailyReminderWorker", "No events found")
                }

            } catch (e: Exception) {
                Log.e("DailyReminderWorker", "Error: ${e.message}")
                return Result.retry()
            }
        } else {
            Log.d("DailyReminderWorker", "Reminder is not active")
        }
        return Result.success()
    }

    private fun showNotification(title: String?, message: String?) {
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_love_solid)
            .setContentTitle("$title")
            .setContentText("Upcoming Event at: $message")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$title"))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.description = CHANNEL_NAME
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}