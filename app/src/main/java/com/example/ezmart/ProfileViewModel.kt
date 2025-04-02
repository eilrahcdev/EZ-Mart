package com.example.ezmart.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ezmart.api.RetrofitClient
import com.example.ezmart.models.ProfileResponse
import com.example.ezmart.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel : ViewModel() {
    val userLiveData = MutableLiveData<User>()

    fun fetchProfile(email: String) {
        RetrofitClient.instance.getProfile(email).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    userLiveData.postValue(response.body()!!.user)
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.e("Profile", "Error: ${t.message}")
            }
        })
    }
}
