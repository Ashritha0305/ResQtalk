package com.example.resqtalk.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.resqtalk.helper.SharedPrefsHelper
import com.example.resqtalk.service.VoiceListenerService
import com.example.resqtalk.service.LocationUpdateService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Boot completed")

            if (context != null) {
                val sharedPrefsHelper = SharedPrefsHelper(context)

                // Start voice listener if enabled
                if (sharedPrefsHelper.isVoiceEnabled()) {
                    val voiceIntent = Intent(context, VoiceListenerService::class.java)
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(voiceIntent)
                        } else {
                            context.startService(voiceIntent)
                        }
                        Log.d("BootReceiver", "VoiceListenerService started")
                    } catch (e: Exception) {
                        Log.e("BootReceiver", "Failed to start VoiceListenerService: ${e.message}")
                    }
                }

                // Start location service if tracking enabled
                if (sharedPrefsHelper.isTrackingEnabled()) {
                    val locationIntent = Intent(context, LocationUpdateService::class.java)
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(locationIntent)
                        } else {
                            context.startService(locationIntent)
                        }
                        Log.d("BootReceiver", "LocationUpdateService started")
                    } catch (e: Exception) {
                        Log.e("BootReceiver", "Failed to start LocationUpdateService: ${e.message}")
                    }
                }
            }
        }
    }
}
