package com.example.grabapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.grabapp.R
import com.example.grabapp.ui.splash.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private fun showNotification(title: String, description: String,dataIntent:String,screen:String) {
        val channelId = getString(R.string.default_notification_channel_id)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "General Notifications", NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notifications from GrabApp"
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if(screen.isNotEmpty())
        {
            val activity = defaultLink+screen
            intent.putExtra(FCM_ACTIVITY,activity)
            intent.putExtra(FCM_DATA,dataIntent)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_vn)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setVibrate(longArrayOf(1000, 0, 1000, 0))
                .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        receiveData(message)
    }

    private fun receiveData(message: RemoteMessage) {
        val data = message.data

        val title = data["title"] ?: "Thông báo mới"
        val body = data["body"] ?: "Bạn có một tin nhắn mới."
        val activityLink = data["screen"]
        val dataIntent = data["intent_data"]

        showNotification(title, body,dataIntent?:"",activityLink?:"")
    }
    companion object{
        val defaultLink = "com.example.grabapp."
        const val FCM_ACTIVITY = "fcm_activity"
        const val FCM_DATA = "fcm_data"
    }
}