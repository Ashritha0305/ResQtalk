package com.example.resqtalk.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.resqtalk.R
import com.example.resqtalk.databinding.ActivityMainBinding
import com.example.resqtalk.helper.LocationHelper
import com.example.resqtalk.helper.SharedPrefsHelper
import com.example.resqtalk.helper.SmsHelper
import com.example.resqtalk.service.VoiceListenerService
import com.example.resqtalk.service.LocationUpdateService
import com.example.resqtalk.data.db.ResQtalkDatabase
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    private lateinit var smsHelper: SmsHelper
    private lateinit var locationHelper: LocationHelper
    private var currentLocation: Location? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                Log.d("MainActivity", "Fine location permission granted")
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                Log.d("MainActivity", "Coarse location permission granted")
            }
        }
    }

    private val smsPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MainActivity", "SMS permission granted")
        }
    }

    private val recordAudioPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MainActivity", "Record audio permission granted")
        }
    }

    private val notificationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.d("MainActivity", "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            sharedPrefsHelper = SharedPrefsHelper(this)
            smsHelper = SmsHelper(this)

            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                locationHelper = LocationHelper(this, fusedLocationClient)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error initializing location helper: ${e.message}")
                Toast.makeText(this, "Location services unavailable", Toast.LENGTH_SHORT).show()
            }

            setupUI()
            updateContactCount()
            requestPermissions()

            // Check if voice listener was enabled before - restart it if so
            if (sharedPrefsHelper.isVoiceEnabled()) {
                startVoiceListenerIfPermitted()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Critical error in onCreate: ${e.message}")
            e.printStackTrace()
            finish()
        }
    }

    private fun startVoiceListenerIfPermitted() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                val intent = Intent(this, VoiceListenerService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
                Log.d("MainActivity", "🎤 Voice listener service restarted on app launch")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error restarting voice listener: ${e.message}")
            }
        } else {
            Log.d("MainActivity", "Record audio permission not granted, skipping voice service")
        }
    }

    private fun setupUI() {
        binding.apply {
            // SOS Button
            btnSos.setOnClickListener {
                triggerManualSOS()
            }

            // Navigation tabs
            navSos.setOnClickListener {
                // Stay on current page
            }

            navContacts.setOnClickListener {
                startActivity(Intent(this@MainActivity, ContactsActivity::class.java))
            }

            navAlerts.setOnClickListener {
                startActivity(Intent(this@MainActivity, AlertsActivity::class.java))
            }

            navSettings.setOnClickListener {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }
        }
    }

    private fun triggerManualSOS() {
        lifecycleScope.launch {
            try {
                // Get location
                val location = locationHelper.getCurrentLocation()
                currentLocation = location

                val mapsLink = if (location != null) {
                    locationHelper.generateMapsLink(location.latitude, location.longitude)
                } else {
                    "Location unavailable"
                }

                // Get SOS message
                val baseMessage = sharedPrefsHelper.getSOSMessage()
                val finalMessage = "$baseMessage\n$mapsLink"

                // Get emergency contacts
                val database = ResQtalkDatabase.getDatabase(this@MainActivity)
                val contacts = database.emergencyContactDao().getEmergencyContacts()
                val phoneNumbers = contacts.map { it.phone }

                if (phoneNumbers.isNotEmpty()) {
                    val result = smsHelper.sendSOStoMultipleContacts(phoneNumbers, finalMessage)
                    Log.d("MainActivity", "SMS Result: Success=${result.successCount}, Fail=${result.failCount}")
                    
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
                    Log.d("MainActivity", "Alert saved to database: recipients=${phoneNumbers.size}, success=${result.successCount}, fail=${result.failCount}")
                    
                    showToast("SOS sent to ${phoneNumbers.size} contacts!")
                } else {
                    showToast("No emergency contacts configured!")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error triggering SOS: ${e.message}")
                showToast("Error sending SOS")
            }
        }
    }

    private fun requestPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("MainActivity", "Fine location permission already granted")
            }
            else -> {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            smsPermissionRequest.launch(Manifest.permission.SEND_SMS)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            recordAudioPermissionRequest.launch(Manifest.permission.RECORD_AUDIO)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        updateContactCount()
    }

    private fun updateContactCount() {
        lifecycleScope.launch {
            try {
                val database = ResQtalkDatabase.getDatabase(this@MainActivity)
                val count = database.emergencyContactDao().getAllContacts().size
                binding.tvContactsCount.text = when (count) {
                    0 -> "0 Contacts"
                    1 -> "1 Contact"
                    else -> "$count Contacts"
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error loading contact count: ${e.message}")
                binding.tvContactsCount.text = "0 Contacts"
            }
        }
    }
}
