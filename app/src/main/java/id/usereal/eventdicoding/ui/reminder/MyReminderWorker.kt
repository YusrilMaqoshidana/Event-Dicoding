package id.usereal.eventdicoding.ui.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import id.usereal.eventdicoding.R.drawable.ic_dicoding
import id.usereal.eventdicoding.data.local.entity.EventEntity
import id.usereal.eventdicoding.data.local.room.FavoriteRoomDatabase
import id.usereal.eventdicoding.data.remote.retrofit.ApiConfig
import id.usereal.eventdicoding.data.repository.EventRepository
import id.usereal.eventdicoding.ui.bottomnavigasi.MainActivity
import id.usereal.eventdicoding.utils.FormatDate

class MyReminderWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {
        const val WORK_NAME = "MyReminderWorker"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "reminder_channel"
    }

    override suspend fun doWork(): Result {
        try {
            val eventRepository = EventRepository.getInstance(
                ApiConfig.getApiService(),
                FavoriteRoomDatabase.getDatabase(applicationContext).eventDao()
            )
            val closestEvents = eventRepository.getNotifEvent()
            if (closestEvents != null) {
                showNotification(closestEvents)
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun showNotification(event: EventEntity) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("eventId", event.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val subStringEventName = if (!event.name.isNullOrEmpty() && event.name.length > 30) {
            "${event.name.substring(0, 30)}..."
        } else {
            event.name ?: "Unknown Event"
        }


        val formattedDate = FormatDate().formatNotificationDateTime(event.beginTime ?: "Selesai")
        val notificationText = "$subStringEventName begins at $formattedDate"

        val notification: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(ic_dicoding)
                .setContentTitle("Upcoming Event")
                .setContentText(notificationText)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            )
            notification.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }
}
