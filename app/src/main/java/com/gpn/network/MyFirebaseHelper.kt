package com.gpn.network

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

object MyFirebaseHelper {

    //TODO: Move all of the backend URL at one common place to manage it better.
    private const val BACKEND_URL = "http://10.0.0.39:8080/api/register/fcm-token"  // Replace with your backend URL

    fun fetchFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String> ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching FCM token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                Log.d("FCM", "FCM Token: $token")

                val userId = getUserId()

                if (userId != null) {
                    sendTokenToServer(userId, token)
                } else {
                    Log.w("FCM", "User ID is null, cannot send FCM token")
                }
            }
    }

    private fun getUserId(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.e("FCM", "❌ User not authenticated! Cannot fetch user ID.")
        }
        return user?.uid
    }

    private fun sendTokenToServer(userId: String, token: String) {

        val client = OkHttpClient()
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "unknown@example.com" // Get user email


        val jsonObject = JSONObject().apply {
            put("userId", userId)
            put("email", userEmail)
            put("fcmToken", token)
        }

        val requestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(BACKEND_URL)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FCM", "Failed to send FCM token to backend", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("FCM", "FCM token successfully sent to backend")
                } else {
                    Log.e("FCM", "Failed to send FCM token. Response: ${response.body?.string()}")
                }
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("FCM", "❌ Failed to send FCM token to backend", e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            Log.d("FCM", "✅ FCM token successfully sent to backend")
                        } else {
                            Log.e(
                                "FCM",
                                "❌ Failed to send FCM token. Response: ${response.body?.string()}"
                            )
                        }
                    }
                })
            }
        })
    }
}
