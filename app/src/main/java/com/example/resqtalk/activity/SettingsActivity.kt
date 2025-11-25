package com.example.resqtalk.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.resqtalk.databinding.ActivitySettingsBinding
import com.example.resqtalk.helper.SharedPrefsHelper
import com.example.resqtalk.service.LocationUpdateService
import com.example.resqtalk.service.VoiceListenerService

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPrefsHelper: SharedPrefsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefsHelper = SharedPrefsHelper(this)

        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            // Load current settings
            etSosMessage.setText(sharedPrefsHelper.getSOSMessage())
            etTriggerWord.setText(sharedPrefsHelper.getTriggerWord())
            switchTracking.isChecked = sharedPrefsHelper.isTrackingEnabled()
            switchVibration.isChecked = sharedPrefsHelper.isVibrationEnabled()
            switchVoiceEnabled.isChecked = sharedPrefsHelper.isVoiceEnabled()
            switchShareLocation.isChecked = true

            // Update trigger word count
            updateTriggerWordCount()

            // Tracking toggle
            switchTracking.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    startLocationTracking()
                } else {
                    stopLocationTracking()
                }
            }

            // Voice enabled toggle
            switchVoiceEnabled.setOnCheckedChangeListener { _, isChecked ->
                sharedPrefsHelper.setVoiceEnabled(isChecked)
            }

            // Trigger word text change
            etTriggerWord.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updateTriggerWordCount()
                }
            }

            // Save settings button
            btnSave.setOnClickListener {
                saveSettings()
            }

            // Reset button
            btnReset.setOnClickListener {
                resetToDefaults()
            }

            // Navigation
            navSos.setOnClickListener {
                startActivity(Intent(this@SettingsActivity, MainActivity::class.java))
                finish()
            }

            navContacts.setOnClickListener {
                startActivity(Intent(this@SettingsActivity, ContactsActivity::class.java))
            }

            navAlerts.setOnClickListener {
                startActivity(Intent(this@SettingsActivity, AlertsActivity::class.java))
            }
        }
    }

    private fun updateTriggerWordCount() {
        val triggerWords = binding.etTriggerWord.text.toString()
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        
        val count = triggerWords.size
        binding.tvTriggerCount.text = if (count == 1) {
            "$count trigger word active"
        } else {
            "$count trigger words active"
        }
    }

    private fun saveSettings() {
        val sosMessage = binding.etSosMessage.text.toString()
        val triggerWord = binding.etTriggerWord.text.toString()
        val trackingEnabled = binding.switchTracking.isChecked
        val vibrationEnabled = binding.switchVibration.isChecked
        val voiceEnabled = binding.switchVoiceEnabled.isChecked

        if (sosMessage.isEmpty()) {
            binding.etSosMessage.setText("I need emergency help!")
        }

        if (triggerWord.isEmpty()) {
            Toast.makeText(this, "Please enter at least one trigger word", Toast.LENGTH_SHORT).show()
            return
        }

        sharedPrefsHelper.setSOSMessage(sosMessage.ifEmpty { "I need emergency help!" })
        sharedPrefsHelper.setTriggerWord(triggerWord)
        sharedPrefsHelper.setTrackingEnabled(trackingEnabled)
        sharedPrefsHelper.setVibrationEnabled(vibrationEnabled)
        sharedPrefsHelper.setVoiceEnabled(voiceEnabled)

        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun resetToDefaults() {
        sharedPrefsHelper.setSOSMessage("I need emergency help!")
        sharedPrefsHelper.setTriggerWord("emergency,help")
        sharedPrefsHelper.setTrackingEnabled(false)
        sharedPrefsHelper.setVibrationEnabled(true)
        sharedPrefsHelper.setVoiceEnabled(false)

        binding.apply {
            etSosMessage.setText("I need emergency help!")
            etTriggerWord.setText("emergency,help")
            switchTracking.isChecked = false
            switchVibration.isChecked = true
            switchVoiceEnabled.isChecked = false
            updateTriggerWordCount()
        }

        Toast.makeText(this, "Settings reset to defaults", Toast.LENGTH_SHORT).show()
    }

    private fun startLocationTracking() {
        try {
            val intent = Intent(this, LocationUpdateService::class.java)
            startService(intent)
            Toast.makeText(this, "Location tracking started", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error starting tracking", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopLocationTracking() {
        try {
            val intent = Intent(this, LocationUpdateService::class.java)
            stopService(intent)
            Toast.makeText(this, "Location tracking stopped", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error stopping tracking", Toast.LENGTH_SHORT).show()
        }
    }
}
