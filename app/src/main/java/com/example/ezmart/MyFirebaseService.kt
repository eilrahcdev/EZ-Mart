package com.example.ezmart.firebase

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ezmart.R
import com.example.ezmart.SplashScreen
import com.example.ezmart.api.RetrofitClient
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

        val title = remoteMessage.data["title"] ?: "New Notification"
        val body = remoteMessage.data["body"] ?: "You have a new message."

        // Log to confirm receipt of notification
        Log.d("FCM_MESSAGE", "Notification received with title: $title, body: $body")

        // Save notification to database
        saveNotificationToServer(body)

        // Display the notification
        sendNotification(title, body)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "New token: $token")
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        val apiService = RetrofitClient.instance
        val request = mapOf("token" to token)

        apiService.updateToken(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("FCM_TOKEN", "Token saved successfully: ${response.body()?.string()}")
                } else {
                    Log.e("FCM_TOKEN", "Failed to save token: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("FCM_TOKEN", "Error saving token", t)
            }
        })
    }

    private fun saveNotificationToServer(message: String) {
        val apiService = RetrofitClient.instance
        val request = mapOf("message" to message, "status" to "unread")

        apiService.saveNotification(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("FCM_SAVE", "Notification saved successfully")
                } else {
                    Log.e("FCM_SAVE", "Failed to save notification: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("FCM_SAVE", "Error saving notification", t)
            }
        })
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendNotification(title: String, messageBody: String) {
        val channelId = "order_notifications"
        val notificationId = Random.nextInt()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Order Alerts", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new orders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, SplashScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.drawable.ez_logo)

        NotificationManagerCompat.from(this).notify(notificationId, notificationBuilder.build())
    }
}
