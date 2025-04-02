package com.example.ezmart.firebase

import com.example.ezmart.api.RetrofitClient
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ezmart.R
import com.example.ezmart.SplashScreen
import com.example.ezmart.utils.UserSession
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class MyFirebaseService : FirebaseMessagingService() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM_MESSAGE", "Message received: ${remoteMessage.data}")

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "New Notification"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "You have a new message."

        sendNotification(title, body)
        saveNotificationToServer(body)
    }

    private fun saveNotificationToServer(message: String) {
        val apiService = RetrofitClient.instance
        val userSession = UserSession(this)
        val userId = userSession.getUserId()

        if (userId == -1) {
            Log.e("FCM_SAVE", "User ID not found in session. Cannot save notification.")
            return
        }

        apiService.saveNotification(
            userId.toString(),
            message,
            "unread"
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("FCM_SAVE", "Notification saved successfully on server")
                    saveNotificationLocally(userId, message) // Save locally too
                } else {
                    Log.e("FCM_SAVE", "Failed to save notification: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("FCM_SAVE", "Error saving notification", t)
            }
        })
    }

    // Save notification to local storage
    private fun saveNotificationLocally(userId: Int, message: String) {
        val sharedPreferences = getSharedPreferences("Notifications_$userId", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val existingNotifications = sharedPreferences.getStringSet("user_notifications", mutableSetOf()) ?: mutableSetOf()
        existingNotifications.add(message)
        editor.putStringSet("user_notifications", existingNotifications)
        editor.apply()

        Log.d("FCM_SAVE", "Notification saved locally for user ID: $userId")
    }


    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendNotification(title: String, messageBody: String) {
        val channelId = "order_notifications"
        val notificationId = Random.Default.nextInt()

        Log.d("FCM_DEBUG", "Creating notification...")

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Order Alerts", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new orders"
            }
            notificationManager.createNotificationChannel(channel)
            Log.d("FCM_DEBUG", "Notification channel created")
        }

        val intent = Intent(this, SplashScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d("FCM_DEBUG", "Building notification...")

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ez_logo)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        Log.d("FCM_DEBUG", "Notifying user...")

        try {
            NotificationManagerCompat.from(this).notify(notificationId, notificationBuilder.build())
            Log.d("FCM_DEBUG", "Notification sent successfully")
        } catch (e: Exception) {
            Log.e("FCM_ERROR", "Failed to send notification", e)
        }
    }
}