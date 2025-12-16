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
import com.example.grabapp.data.OrderStorage
import com.example.grabapp.data.TokenStorage
import com.example.grabapp.driver.home.DriverHomeActivity
import com.example.grabapp.driver.login.DriverLoginActivity
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
        const val ACTION_ORDER_CANCELLED = "com.example.grabapp.ORDER_CANCELLED"
        const val EXTRA_ORDER_ID = "order_id"
        const val EXTRA_EVENT_TYPE = "eventType"
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

        // Navigate trực tiếp đến DriverHomeActivity nếu đã login, hoặc DriverLoginActivity nếu chưa login
        val isValidOrderId = orderID.isNotEmpty() && orderID != "Bạn có một tin nhắn mới."
        val tokenStorage = TokenStorage(this)
        val targetActivity = if (tokenStorage.hasToken()) {
            // Đã login, navigate đến DriverHomeActivity
            DriverHomeActivity::class.java
        } else {
            // Chưa login, navigate đến DriverLoginActivity
            DriverLoginActivity::class.java
        }
        
        if (isValidOrderId) {
            val orderStorage = OrderStorage(this)
            orderStorage.saveActiveOrderId(orderID)
        }
        
        val intent = Intent(this, targetActivity).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            if (isValidOrderId) {
                putExtra("notification_order_id", orderID)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            if (isValidOrderId) orderID.hashCode().and(0x7FFFFFFF) else 0,
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

        val title = data["title"] ?: data["status"] ?: "Thông báo mới"
        val orderId = data["orderID"] ?: data["orderId"] ?: "Bạn có một tin nhắn mới."
        val messageText = data["message"]
        val eventType = data["eventType"]

        Log.d("FCM_Service", "=== Nhận notification ===")
        Log.d("FCM_Service", "Title: $title")
        Log.d("FCM_Service", "OrderID: $orderId")
        Log.d("FCM_Service", "Message: $messageText")
        Log.d("FCM_Service", "EventType: $eventType")
        Log.d("FCM_Service", "All data: $data")

        showNotification(title, orderId, messageText ?: "")

        // Xử lý theo eventType
        val isValidOrderId = orderId.isNotEmpty() && orderId != "Bạn có một tin nhắn mới."
        
        when (eventType) {
            "ORDER_CANCELLED" -> {
                if (isValidOrderId) {
                    sendOrderCancelledBroadcast(orderId)
                }
            }
            else -> {
                // Xử lý new order như cũ
                if (isValidOrderId) {
                    sendNewOrderBroadcast(orderId)
                } else {
                    Log.d("FCM_Service", "OrderID không hợp lệ, không gửi broadcast")
                }
            }
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
    
    private fun sendOrderCancelledBroadcast(orderID: String) {
        Log.d("FCM_Service", "=== Gửi broadcast ORDER_CANCELLED ===")
        Log.d("FCM_Service", "Action: $ACTION_ORDER_CANCELLED")
        Log.d("FCM_Service", "OrderID: $orderID")
        Log.d("FCM_Service", "Package: $packageName")
        
        val intent = Intent(ACTION_ORDER_CANCELLED).apply {
            putExtra(EXTRA_ORDER_ID, orderID)
            setPackage(packageName)
        }
        sendBroadcast(intent)
        Log.d("FCM_Service", "Broadcast ORDER_CANCELLED đã được gửi")
    }
}