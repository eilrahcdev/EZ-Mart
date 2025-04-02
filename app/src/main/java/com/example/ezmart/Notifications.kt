package com.example.ezmart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezmart.api.RetrofitClient
import com.example.ezmart.models.NotificationModel
import com.example.ezmart.utils.UserSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Notifications : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var emptyTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.notification_activity)

        recyclerView = findViewById(R.id.recyclerView_notifications)
        emptyTextView = findViewById(R.id.emptyTextView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NotificationAdapter(mutableListOf())
        recyclerView.adapter = adapter

        fetchNotifications()

        // Back Button Functionality
        findViewById<ImageButton>(R.id.backBtn_notification).setOnClickListener {
            startActivity(Intent(this, Orders::class.java))
            finish()
        }
    }

    private fun fetchNotifications() {
        val userSession = UserSession(this)
        val userId = userSession.getUserId()

        if (userId == -1) {
            Log.e("NOTIFICATION_FETCH", "User ID not found in session. Cannot fetch notifications.")
            showError()
            return
        }

        // Fetch from local storage
        val sharedPreferences = getSharedPreferences("Notifications_$userId", MODE_PRIVATE)
        val localNotifications = sharedPreferences.getStringSet("user_notifications", null)

        if (localNotifications != null && localNotifications.isNotEmpty()) {
            val notificationList = localNotifications.map { message ->
                NotificationModel(
                    id = 0,
                    user_id = userId,
                    message = message,
                    status = "unread",
                    created_at = ""
                )
            }
            adapter.updateNotifications(notificationList)
            recyclerView.visibility = View.VISIBLE
            emptyTextView.visibility = View.GONE
        } else {
            fetchNotificationsFromServer(userId)
        }

    }

    private fun fetchNotificationsFromServer(userId: Int) {
        RetrofitClient.instance.getNotifications(userId).enqueue(object : Callback<List<NotificationModel>> {
            override fun onResponse(call: Call<List<NotificationModel>>, response: Response<List<NotificationModel>>) {
                if (response.isSuccessful && response.body() != null) {
                    val notifications = response.body()!!

                    if (notifications.isNotEmpty()) {
                        adapter.updateNotifications(notifications)
                        recyclerView.visibility = View.VISIBLE
                        emptyTextView.visibility = View.GONE
                    } else {
                        recyclerView.visibility = View.GONE
                        emptyTextView.text = "No notifications available."
                        emptyTextView.visibility = View.VISIBLE
                    }
                } else {
                    Log.e("NOTIFICATION_FETCH", "Failed to fetch notifications: ${response.errorBody()?.string()}")
                    showError()
                }
            }

            override fun onFailure(call: Call<List<NotificationModel>>, t: Throwable) {
                Log.e("NOTIFICATION_FETCH", "Failed to load notifications", t)
                showError()
            }
        })
    }

    private fun showError() {
        recyclerView.visibility = View.GONE
        emptyTextView.text = "Failed to load notifications"
        emptyTextView.visibility = View.VISIBLE
    }
}
