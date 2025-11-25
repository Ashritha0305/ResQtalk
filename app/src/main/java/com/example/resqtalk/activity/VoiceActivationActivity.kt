package com.example.resqtalk.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.resqtalk.databinding.ActivityVoiceActivationBinding
import com.example.resqtalk.helper.SharedPrefsHelper
import com.example.resqtalk.service.VoiceListenerService

class VoiceActivationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVoiceActivationBinding
    private lateinit var sharedPrefsHelper: SharedPrefsHelper

    private val recordAudioPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("VoiceActivationActivity", "Record audio permission granted")
            startVoiceListener()
        } else {
            Toast.makeText(this, "Record audio permission required for voice trigger", Toast.LENGTH_SHORT).show()
            binding.switchVoiceEnabled.isChecked = false
        }
    }

    private val notificationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("VoiceActivationActivity", "Notification permission granted")
            startVoiceListener()
        } else {
            Toast.makeText(this, "Notification permission required to run voice listener", Toast.LENGTH_SHORT).show()
            binding.switchVoiceEnabled.isChecked = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceActivationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefsHelper = SharedPrefsHelper(this)

        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            // Load current trigger word
            etTriggerWord.setText(sharedPrefsHelper.getTriggerWord())

            // Toggle voice activation
            switchVoiceEnabled.isChecked = sharedPrefsHelper.isVoiceEnabled()

            switchVoiceEnabled.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Request permission before starting
                    if (ContextCompat.checkSelfPermission(
                            this@VoiceActivationActivity,
                            Manifest.permission.RECORD_AUDIO
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        recordAudioPermissionRequest.launch(Manifest.permission.RECORD_AUDIO)
                    } else {
                        startVoiceListener()
                    }
                } else {
                    stopVoiceListener()
                }
                sharedPrefsHelper.setVoiceEnabled(isChecked)
            }

            // Save settings
            btnSave.setOnClickListener {
                saveSettings()
            }

            // Test trigger word
            btnTestTrigger.setOnClickListener {
                testTriggerWord()
            }

            // Back button
            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun saveSettings() {
        val triggerWord = binding.etTriggerWord.text.toString()

        if (triggerWord.isEmpty()) {
            Toast.makeText(this, "Please enter a trigger word", Toast.LENGTH_SHORT).show()
            return
        }

        sharedPrefsHelper.setTriggerWord(triggerWord)
        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show()

        // Restart service with new trigger word
        if (binding.switchVoiceEnabled.isChecked) {
            stopVoiceListener()
            startVoiceListener()
        }
    }

    private fun startVoiceListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("VoiceActivationActivity", "Requesting POST_NOTIFICATIONS permission")
                notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("VoiceActivationActivity", "Requesting RECORD_AUDIO permission")
            recordAudioPermissionRequest.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        try {
            val intent = Intent(this, VoiceListenerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            Toast.makeText(this, "Voice listener started", Toast.LENGTH_SHORT).show()
            Log.d("VoiceActivationActivity", "🎤 Voice listener service started")
        } catch (e: Exception) {
            Log.e("VoiceActivationActivity", "Error starting voice listener: ${e.message}")
            Toast.makeText(this, "Error starting voice listener", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopVoiceListener() {
        try {
            val intent = Intent(this, VoiceListenerService::class.java)
            stopService(intent)
            Toast.makeText(this, "Voice listener stopped", Toast.LENGTH_SHORT).show()
            Log.d("VoiceActivationActivity", "🎤 Voice listener service stopped")
        } catch (e: Exception) {
            Log.e("VoiceActivationActivity", "Error stopping voice listener: ${e.message}")
        }
    }

    private fun testTriggerWord() {
        Toast.makeText(
            this,
            "Speak the trigger word: ${binding.etTriggerWord.text}",
            Toast.LENGTH_LONG
        ).show()
    }
}
