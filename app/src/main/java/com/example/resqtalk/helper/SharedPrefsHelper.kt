package com.example.resqtalk.helper

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "resqtalk_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_TRIGGER_WORD = "trigger_word"
        private const val KEY_SOS_MESSAGE = "sos_message"
        private const val KEY_VOICE_ENABLED = "voice_enabled"
        private const val KEY_TRACKING_ENABLED = "tracking_enabled"
        private const val KEY_ALERT_TONE = "alert_tone"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val DEFAULT_SOS_MESSAGE = "HELP! I need assistance! My location: "
        private const val DEFAULT_TRIGGER_WORD = "Help"
    }

    // Trigger Word
    fun setTriggerWord(word: String) {
        sharedPreferences.edit().putString(KEY_TRIGGER_WORD, word).apply()
    }

    fun getTriggerWord(): String {
        return sharedPreferences.getString(KEY_TRIGGER_WORD, DEFAULT_TRIGGER_WORD) ?: DEFAULT_TRIGGER_WORD
    }

    // SOS Message
    fun setSOSMessage(message: String) {
        sharedPreferences.edit().putString(KEY_SOS_MESSAGE, message).apply()
    }

    fun getSOSMessage(): String {
        return sharedPreferences.getString(KEY_SOS_MESSAGE, DEFAULT_SOS_MESSAGE) ?: DEFAULT_SOS_MESSAGE
    }

    // Voice Activation
    fun setVoiceEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_VOICE_ENABLED, enabled).apply()
    }

    fun isVoiceEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_VOICE_ENABLED, false)
    }

    // Tracking
    fun setTrackingEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_TRACKING_ENABLED, enabled).apply()
    }

    fun isTrackingEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_TRACKING_ENABLED, false)
    }

    // Alert Tone
    fun setAlertTone(toneUri: String) {
        sharedPreferences.edit().putString(KEY_ALERT_TONE, toneUri).apply()
    }

    fun getAlertTone(): String {
        return sharedPreferences.getString(KEY_ALERT_TONE, "") ?: ""
    }

    // Vibration
    fun setVibrationEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply()
    }

    fun isVibrationEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_VIBRATION_ENABLED, true)
    }
}
