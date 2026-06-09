package org.worklog.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.worklog.app.core.notification.RefreshEvents
import org.worklog.app.domain.usecase.notification.SaveDeviceTokenUseCase

class WorkLogFirebaseMessagingService : FirebaseMessagingService() {

    private val saveDeviceTokenUseCase: SaveDeviceTokenUseCase by inject()
    private val refreshEvents: RefreshEvents by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            saveDeviceTokenUseCase(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "WorkLog"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""
        val badgeCount = remoteMessage.data["badge_count"]?.toIntOrNull() ?: 0

        showNotification(title, body, badgeCount)

        // Translate the server's data.type into refresh topics so any active
        // ViewModel subscribed to that topic pulls fresh data immediately.
        // ViewModels that aren't in memory will still see the change next
        // time the screen resumes (existing LifecycleResumeEffect behaviour).
        RefreshEvents.topicsFor(remoteMessage.data["type"]).forEach { topic ->
            refreshEvents.emit(topic)
        }
    }

    private fun showNotification(title: String, body: String, badgeCount: Int = 0) {
        val channelId = "worklog_notifications"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "WorkLog Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications from WorkLog"
                enableLights(true)
                enableVibration(true)
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .setNumber(badgeCount)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
