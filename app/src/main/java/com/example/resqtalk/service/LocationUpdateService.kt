package com.example.resqtalk.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.resqtalk.R
import com.example.resqtalk.activity.MainActivity
import com.example.resqtalk.helper.SharedPrefsHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationServices

class LocationUpdateService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    private var locationCallback: LocationCallback? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("LocationUpdateService", "Service created")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sharedPrefsHelper = SharedPrefsHelper(this)

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        startLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LocationUpdateService", "Service started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LocationUpdateService", "Service destroyed")
        stopLocationUpdates()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                60000 // 1 minute
            ).apply {
                setMinUpdateIntervalMillis(30000) // 30 seconds
                setMaxUpdateDelayMillis(120000) // 2 minutes
            }.build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        Log.d(
                            "LocationUpdateService",
                            "Location: ${location.latitude}, ${location.longitude}"
                        )
                        // Store location or broadcast it
                        val intent = Intent("com.example.resqtalk.LOCATION_UPDATE").apply {
                            putExtra("latitude", location.latitude)
                            putExtra("longitude", location.longitude)
                        }
                        sendBroadcast(intent)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e("LocationUpdateService", "Location permission denied: ${e.message}")
        }
    }

    private fun stopLocationUpdates() {
        try {
            if (locationCallback != null) {
                fusedLocationClient.removeLocationUpdates(locationCallback!!)
            }
        } catch (e: Exception) {
            Log.e("LocationUpdateService", "Error stopping updates: ${e.message}")
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ResQtalk Location Tracking")
            .setContentText("Your location is being tracked")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "ResQtalk Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1003
        private const val CHANNEL_ID = "resqtalk_location_channel"
    }
}
