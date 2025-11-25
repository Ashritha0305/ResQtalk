package com.example.resqtalk.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.resqtalk.R
import com.example.resqtalk.databinding.ActivitySendAlertBinding
import com.example.resqtalk.helper.LocationHelper
import com.example.resqtalk.helper.SharedPrefsHelper
import com.example.resqtalk.helper.SmsHelper
import com.example.resqtalk.data.db.ResQtalkDatabase
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class SendAlertActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySendAlertBinding
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    private lateinit var smsHelper: SmsHelper
    private lateinit var locationHelper: LocationHelper
    private var currentLocation: Location? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fetchCurrentLocation()
        }
    }

    private val smsPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            performSendAlert()
        } else {
            Toast.makeText(this, "SMS permission required to send alerts", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefsHelper = SharedPrefsHelper(this)
        smsHelper = SmsHelper(this)

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationHelper = LocationHelper(this, fusedLocationClient)

        setupUI()
        loadEmergencyContacts()
    }

    private fun setupUI() {
        binding.apply {
            // Set default message
            etMessage.setText(sharedPrefsHelper.getSOSMessage())

            // Toggle for location
            switchIncludeLocation.isChecked = true

            btnSendAlert.setOnClickListener {
                sendAlert()
            }

            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun loadEmergencyContacts() {
        lifecycleScope.launch {
            try {
                val database = ResQtalkDatabase.getDatabase(this@SendAlertActivity)
                val contacts = database.emergencyContactDao().getEmergencyContacts()

                val contactNames = contacts.map { it.name }
                val adapter = android.widget.ArrayAdapter(
                    this@SendAlertActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    contactNames
                )
                binding.spinnerContacts.adapter = adapter
            } catch (e: Exception) {
                Log.e("SendAlertActivity", "Error loading contacts: ${e.message}")
            }
        }
    }

    private fun sendAlert() {
        // Check SMS permission first
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            smsPermissionRequest.launch(Manifest.permission.SEND_SMS)
            return
        }

        performSendAlert()
    }

    private fun performSendAlert() {
        val message = binding.etMessage.text.toString()
        val includeLocation = binding.switchIncludeLocation.isChecked

        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                var finalMessage = message

                // Add location if enabled
                if (includeLocation) {
                    if (ContextCompat.checkSelfPermission(
                            this@SendAlertActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val location = locationHelper.getCurrentLocation()
                        if (location != null) {
                            val mapsLink = locationHelper.generateMapsLink(
                                location.latitude,
                                location.longitude
                            )
                            finalMessage = "$message\n$mapsLink"
                            currentLocation = location
                        }
                    } else {
                        locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        return@launch
                    }
                }

                // Get selected contact
                val selectedContactName = binding.spinnerContacts.selectedItem as? String
                if (selectedContactName != null) {
                    val database = ResQtalkDatabase.getDatabase(this@SendAlertActivity)
                    val contacts = database.emergencyContactDao().getEmergencyContacts()
                    val selectedContact = contacts.find { it.name == selectedContactName }

                    if (selectedContact != null) {
                        smsHelper.sendSMS(selectedContact.phone, finalMessage)
                        Toast.makeText(this@SendAlertActivity, "Alert sent!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this@SendAlertActivity, "No contacts available", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("SendAlertActivity", "Error sending alert: ${e.message}")
                Toast.makeText(this@SendAlertActivity, "Error sending alert", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchCurrentLocation() {
        lifecycleScope.launch {
            try {
                val location = locationHelper.getCurrentLocation()
                if (location != null) {
                    currentLocation = location
                }
            } catch (e: Exception) {
                Log.e("SendAlertActivity", "Error fetching location: ${e.message}")
            }
        }
    }
}
