package com.example.grabapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import com.example.grabapp.R
import com.example.grabapp.ui.splash.SplashActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private fun showNotification(status: String, message: String, orderID: String) {
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
        intent.putExtra(FCM_DATA, orderID)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_delivery)
                .setContentTitle(status)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setVibrate(longArrayOf(1000, 0, 1000, 0))
                .build()
        val intentData = Intent("com.example.grabapp.NEW_ORDER_UPDATE")
        intentData.putExtra("orderID", orderID)
        sendBroadcast(intent)
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        receiveData(message)
    }

    private fun receiveData(message: RemoteMessage) {
        val data = message.data
        val status = data["status"] ?: "Thông báo mới"
        val orderID = data["orderID"] ?: ""
        val message = data["message"] ?: ""
        showNotification(status, message, orderID)
    }

    companion object {
        const val FCM_ACTIVITY = "fcm_activity"
        const val FCM_DATA = "fcm_data"
    }
}