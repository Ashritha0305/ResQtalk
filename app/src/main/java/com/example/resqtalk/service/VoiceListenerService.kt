package com.example.resqtalk.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.resqtalk.R
import com.example.resqtalk.activity.MainActivity
import com.example.resqtalk.helper.LocationHelper
import com.example.resqtalk.helper.SharedPrefsHelper
import com.example.resqtalk.helper.SmsHelper
import com.example.resqtalk.helper.VoiceTriggerHelper
import com.example.resqtalk.data.db.ResQtalkDatabase
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VoiceListenerService : Service() {

    private lateinit var voiceTriggerHelper: VoiceTriggerHelper
    private lateinit var locationHelper: LocationHelper
    private lateinit var smsHelper: SmsHelper
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    private lateinit var database: ResQtalkDatabase
    private var wakeLock: PowerManager.WakeLock? = null

    private val serviceScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        Log.d("VoiceListenerService", "🎤 Voice Listener Service created")

        database = ResQtalkDatabase.getDatabase(this)
        sharedPrefsHelper = SharedPrefsHelper(this)
        smsHelper = SmsHelper(this)

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationHelper = LocationHelper(this, fusedLocationClient)

        // Acquire wake lock to keep service running even when screen is off
        acquireWakeLock()

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        initializeVoiceListener()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("VoiceListenerService", "🎤 Voice Listener Service started")
        // START_STICKY keeps service running even if system kills it
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("VoiceListenerService", "🎤 Voice Listener Service destroyed")
        voiceTriggerHelper.destroy()
        releaseWakeLock()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        try {
            if (sharedPrefsHelper.isVoiceEnabled()) {
                val restartIntent = Intent(applicationContext, VoiceListenerService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    applicationContext.startForegroundService(restartIntent)
                } else {
                    applicationContext.startService(restartIntent)
                }
                Log.d("VoiceListenerService", "Service restarted after task removed")
            }
        } catch (e: Exception) {
            Log.e("VoiceListenerService", "Failed to restart service: ${e.message}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun acquireWakeLock() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "resqtalk:voice_listener"
            )
            wakeLock?.acquire() // Keep wake lock indefinitely
            Log.d("VoiceListenerService", "✅ Wake lock acquired")
        } catch (e: Exception) {
            Log.e("VoiceListenerService", "Error acquiring wake lock: ${e.message}")
        }
    }

    private fun releaseWakeLock() {
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
                Log.d("VoiceListenerService", "✅ Wake lock released")
            }
        } catch (e: Exception) {
            Log.e("VoiceListenerService", "Error releasing wake lock: ${e.message}")
        }
    }

    private fun initializeVoiceListener() {
        val triggerWord = sharedPrefsHelper.getTriggerWord()
        Log.d("VoiceListenerService", "🎤 Setting up voice listener for trigger: '$triggerWord'")
        
        voiceTriggerHelper = VoiceTriggerHelper(
            this,
            triggerWord,
            onTriggerDetected = { triggerSOS() }
        )

        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 0L)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        }

        voiceTriggerHelper.startListening(recognizerIntent)
    }

    private fun triggerSOS() {
        Log.d("VoiceListenerService", "🚨 SOS triggered by voice command!")

        serviceScope.launch {
            try {
                // Get current location
                val location = locationHelper.getCurrentLocation()
                val mapsLink = if (location != null) {
                    locationHelper.generateMapsLink(location.latitude, location.longitude)
                } else {
                    "Location unavailable"
                }

                // Get SOS message
                val baseMessage = sharedPrefsHelper.getSOSMessage()
                val finalMessage = "$baseMessage\n$mapsLink"

                // Get emergency contacts
                val contacts = database.emergencyContactDao().getEmergencyContacts()
                val phoneNumbers = contacts.map { it.phone }

                // Send SMS
                if (phoneNumbers.isNotEmpty()) {
                    val result = smsHelper.sendSOStoMultipleContacts(phoneNumbers, finalMessage)
                    Log.d("VoiceListenerService", "SOS sent to ${phoneNumbers.size} contacts - Success: ${result.successCount}, Fail: ${result.failCount}")
                    
                    // Save to alert history
                    val alert = com.example.resqtalk.data.entity.AlertHistory(
                        timestamp = System.currentTimeMillis(),
                        message = baseMessage,
                        recipientCount = phoneNumbers.size,
                        successCount = result.successCount,
                        failCount = result.failCount,
                        location = mapsLink
                    )
                    database.alertHistoryDao().insertAlert(alert)
                    Log.d("VoiceListenerService", "Alert saved to database via voice trigger")
                }

                // Play notification
                playSOSNotification()

            } catch (e: Exception) {
                Log.e("VoiceListenerService", "Error triggering SOS: ${e.message}")
            }
        }
    }

    private fun playSOSNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val sosNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SOS Alert Sent!")
            .setContentText("Emergency alert sent to your contacts")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(SOS_NOTIFICATION_ID, sosNotification)
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
            .setContentTitle("ResQtalk Voice Listener")
            .setContentText("Listening for trigger word...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "ResQtalk Voice Listener",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val SOS_NOTIFICATION_ID = 1002
        private const val CHANNEL_ID = "resqtalk_voice_channel"
    }
}
