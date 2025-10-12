package com.example.grabapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.grabapp.R
import com.example.grabapp.ui.splash.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Hàm getRemoteView() đã được xóa vì không cần thiết nữa.

    private fun showNotification(title: String, description: String) {
        val channelId = getString(R.string.default_notification_channel_id)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tạo Notification Channel (cần cho Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "General Notifications", NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notifications from GrabApp"
            manager.createNotificationChannel(channel)
        }

        // Intent để mở SplashActivity khi người dùng nhấp vào thông báo
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Cờ này giúp quản lý stack activity
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Xây dựng thông báo cơ bản
        val notification =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_vn) // Bắt buộc: icon nhỏ cho thông báo
                .setContentTitle(title) // THAY ĐỔI: Đặt tiêu đề cho thông báo
                .setContentText(description) // THAY ĐỔI: Đặt nội dung cho thông báo
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Ưu tiên cao
                .setAutoCancel(true) // Tự động xóa khi người dùng nhấp vào
                .setContentIntent(pendingIntent) // Gán hành động khi nhấp vào
                .setOnlyAlertOnce(true)
                .setVibrate(longArrayOf(1000, 0, 1000, 0))
                .build()

        // Hiển thị thông báo
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        receiveData(message)
    }

    private fun receiveData(message: RemoteMessage) {
        val data = message.data
        // Giả sử server gửi "title" và "body" trong data payload
        val title = data["title"] ?: "Thông báo mới" // Lấy title từ data, nếu không có thì dùng mặc định
        val body = data["body"] ?: "Bạn có một tin nhắn mới." // Lấy body từ data

        // Gọi hàm hiển thị thông báo
        showNotification(title, body)
    }
}