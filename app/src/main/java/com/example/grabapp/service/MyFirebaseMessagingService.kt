package com.example.grabapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.grabapp.R
import com.example.grabapp.ui.splash.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "default_channel"
        private const val CHANNEL_NAME = "General Notifications"
        private const val NOTIFICATION_ID = 1001
        val defaultLink = "com.example.grabapp."
        const val FCM_ACTIVITY = "fcm_activity"
        const val FCM_DATA = "fcm_data"
        const val ACTION_NEW_ORDER = "com.example.grabapp.NEW_ORDER"
        const val EXTRA_ORDER_ID = "order_id"
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Kiểm tra xem channel đã tồn tại chưa
            val existingChannel = manager.getNotificationChannel(CHANNEL_ID)
            if (existingChannel != null) {
                return // Channel đã tồn tại, không cần tạo lại
            }

            // Tạo channel với IMPORTANCE_HIGH để hiển thị heads-up notification
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notifications from GrabApp"

            // Bật sound và lights để đảm bảo hiển thị heads-up
            channel.enableLights(true)
            channel.enableVibration(true)

            // Thiết lập sound mặc định
            val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            channel.setSound(soundUri, audioAttributes)

            // Thiết lập vibration pattern
            channel.vibrationPattern = longArrayOf(1000, 0, 1000, 0)

            // Tạo channel
            manager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, orderID: String, message: String) {
        // Đảm bảo channel được tạo trước khi hiển thị notification
        createNotificationChannel()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_vn)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setDefaults(NotificationCompat.DEFAULT_ALL) // Bao gồm sound, vibration, lights
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(false) // Cho phép alert mỗi lần để đảm bảo hiển thị
                .setVibrate(longArrayOf(1000, 0, 1000, 0))
                .setCategory(NotificationCompat.CATEGORY_MESSAGE) // Phân loại notification
                .setFullScreenIntent(null, true)
                .build()

        // Sử dụng ID cố định hoặc dựa trên orderID để tránh spam
        val notificationId = if (orderID.isNotEmpty() && orderID != "Bạn có một tin nhắn mới.") {
            orderID.hashCode().and(0x7FFFFFFF) // Đảm bảo số dương
        } else {
            NOTIFICATION_ID
        }

        manager.notify(notificationId, notification)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        receiveData(message)
    }

    private fun receiveData(message: RemoteMessage) {
        val data = message.data

        val title = data["status"] ?: "Thông báo mới"
        val orderID = data["orderID"] ?: "Bạn có một tin nhắn mới."
        val message = data["message"]

        Log.d("FCM_Service", "=== Nhận notification ===")
        Log.d("FCM_Service", "Title: $title")
        Log.d("FCM_Service", "OrderID: $orderID")
        Log.d("FCM_Service", "Message: $message")
        Log.d("FCM_Service", "All data: $data")

        showNotification(title, orderID, message ?: "")

        // Gửi broadcast để hiển thị dialog nếu có orderID hợp lệ
        val isValidOrderID = orderID.isNotEmpty() && orderID != "Bạn có một tin nhắn mới."
        Log.d("FCM_Service", "OrderID hợp lệ: $isValidOrderID")
        
        if (isValidOrderID) {
            sendNewOrderBroadcast(orderID)
        } else {
            Log.d("FCM_Service", "OrderID không hợp lệ, không gửi broadcast")
        }
    }

    private fun sendNewOrderBroadcast(orderID: String) {
        Log.d("FCM_Service", "=== Gửi broadcast ===")
        Log.d("FCM_Service", "Action: $ACTION_NEW_ORDER")
        Log.d("FCM_Service", "OrderID: $orderID")
        Log.d("FCM_Service", "Package: $packageName")
        
        val intent = Intent(ACTION_NEW_ORDER).apply {
            putExtra(EXTRA_ORDER_ID, orderID)
            setPackage(packageName)
        }
        sendBroadcast(intent)
        Log.d("FCM_Service", "Broadcast đã được gửi")
    }
}