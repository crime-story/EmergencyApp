package com.lifeSavers.emergencyapp.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lifeSavers.emergencyapp.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class MyFirebaseMessagingService : FirebaseMessagingService() {
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder

    override fun onNewToken(s: String) {
        Log.d("device_token", s)
        val sharedPreferences = getSharedPreferences("com.lifeSavers.emergencyapp", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("device_token", s)
            apply()
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        super.onMessageReceived(remoteMessage)
//        // Check if message contains a data payload.
//        if (remoteMessage.data.isNotEmpty()) {
//            val title = remoteMessage.data["title"]
//            val body = remoteMessage.data["body"]
//
//            // Create notification
//            val notificationBuilder = NotificationCompat.Builder(this, "channelId")
//                .setContentTitle(title)
//                .setContentText(body)
//                    //tochange
//                .setSmallIcon(R.drawable.ic_attachment)
//
//            // Show notification
//            val notificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.notify(0, notificationBuilder.build())
//        }

        if (remoteMessage.notification != null) {
            showNotification(remoteMessage.notification!!.title.toString(), remoteMessage.notification!!.body.toString())
            Log.d("notification", remoteMessage.notification.toString())
        }
    }

    fun showNotification(title: String, messageBody: String) {
        val intent = Intent(this, SignUpActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(title, messageBody, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, title)
//                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
        } else {
            builder = Notification.Builder(this)
//                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234, builder.build())
    }

    fun sendNotification(senderName: String) {
        val serverKey = "AAAAISZe1qo:APA91bEEPKBySrq7D_VfTdPX3vAbp3kahRgnYYoO17dfmG1qwFqqZ-1IiQj8TFuvjmO-mqXcKGb6e_Qo4ZdebgHaxR3Wn_NvNbDGWJCBUReFpPVWt-1RcEGoxYWqhc8kPlc2-UnHhSv9"
        val projectId = "emergencyapp-3a6bd"
        val deviceToken = getSharedPreferences("com.lifeSavers.emergencyapp", MODE_PRIVATE).getString("device_token", null)
        val client = OkHttpClient()

        val payload = """
          {
            "message": {
              "token": "$deviceToken",
              "notification": {
                "title": "Emergency App",
                "body": "You got a new message from "$senderName"
              }
            }
          }
        """.trimIndent()
// https://stackoverflow.com/questions/37490629/firebase-send-notification-with-rest-api
        val request = Request.Builder()
            .url("https://fcm.googleapis.com/v1/projects/$projectId/messages:send")
            .header("Authorization", "Bearer $serverKey")
            .header("Content-Type", "application/json")
            .post(payload.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Unexpected response code: ${response.code}")
            }

            val responseBody = response.body?.string()
            // Process the response body as needed
        }

    }
}
