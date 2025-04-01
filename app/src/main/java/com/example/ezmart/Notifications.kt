package com.example.ezmart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezmart.api.RetrofitClient
import com.example.ezmart.models.NotificationModel
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
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun fetchNotifications() {
        RetrofitClient.instance.getNotifications().enqueue(object : Callback<List<NotificationModel>> {
            override fun onResponse(call: Call<List<NotificationModel>>, response: Response<List<NotificationModel>>) {
                if (response.isSuccessful && response.body() != null) {
                    val notifications = response.body()!!
                    if (notifications.isNotEmpty()) {
                        adapter.updateNotifications(notifications)
                        emptyTextView.visibility = TextView.GONE
                    } else {
                        emptyTextView.text = "No notifications available"
                    }
                }
            }

            override fun onFailure(call: Call<List<NotificationModel>>, t: Throwable) {
                Log.e("NOTIFICATION_FETCH", "Failed to load notifications", t)
            }
        })
    }
}
