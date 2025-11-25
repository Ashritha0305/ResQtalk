package com.example.resqtalk.helper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

class VoiceTriggerHelper(
    private val context: Context,
    private val triggerWord: String,
    private val onTriggerDetected: () -> Unit
) : RecognitionListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var listeningIntent: Intent? = null
    private var isDestroyed = false
    private val handler = Handler(Looper.getMainLooper())
    private var restartRunnable: Runnable? = null

    fun startListening(intent: android.content.Intent) {
        listeningIntent = intent
        startListeningInternal()
    }

    private fun startListeningInternal() {
        try {
            if (isDestroyed) {
                Log.d("VoiceTrigger", "Helper destroyed, not restarting")
                return
            }

            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                Log.e("VoiceTrigger", "Speech recognition not available")
                scheduleRestart()
                return
            }

            if (speechRecognizer == null) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                speechRecognizer?.setRecognitionListener(this)
            }

            Log.d("VoiceTrigger", "Starting continuous listening for: $triggerWord")
            speechRecognizer?.startListening(listeningIntent)
        } catch (e: Exception) {
            Log.e("VoiceTrigger", "Error starting listening: ${e.message}")
            scheduleRestart()
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e("VoiceTrigger", "Error stopping listening: ${e.message}")
        }
    }

    fun destroy() {
        try {
            isDestroyed = true
            handler.removeCallbacks(restartRunnable!!)
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
            Log.d("VoiceTrigger", "Helper destroyed")
        } catch (e: Exception) {
            Log.e("VoiceTrigger", "Error destroying recognizer: ${e.message}")
        }
    }

    private fun scheduleRestart() {
        if (isDestroyed) return

        restartRunnable = Runnable {
            if (!isDestroyed) {
                Log.d("VoiceTrigger", "Restarting listening...")
                startListeningInternal()
            }
        }

        handler.postDelayed(restartRunnable!!, 1000) // Restart after 1 second
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Log.d("VoiceTrigger", "Ready for speech")
    }

    override fun onBeginningOfSpeech() {
        Log.d("VoiceTrigger", "Beginning of speech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        // Sound level changes
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        // Buffer received
    }

    override fun onEndOfSpeech() {
        Log.d("VoiceTrigger", "End of speech - restarting listening")
        // Automatically restart listening after speech ends
        scheduleRestart()
    }

    override fun onError(error: Int) {
        Log.e("VoiceTrigger", "Recognition error: $error")
        // Restart listening on error (codes: 1=network, 6=no match, 7=audio, 8=timeout, 9=no match, 11=server)
        // All errors should restart the listener
        scheduleRestart()
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches != null && matches.isNotEmpty()) {
            val spokenText = matches[0].lowercase()
            Log.d("VoiceTrigger", "Recognized: $spokenText")

            if (spokenText.contains(triggerWord.lowercase())) {
                Log.d("VoiceTrigger", "🎤 TRIGGER WORD DETECTED: $triggerWord")
                onTriggerDetected()
            }
        }

        // Always restart listening to continuously monitor
        scheduleRestart()
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches != null && matches.isNotEmpty()) {
            val spokenText = matches[0].lowercase()
            Log.d("VoiceTrigger", "Partial: $spokenText")
            
            if (spokenText.contains(triggerWord.lowercase())) {
                Log.d("VoiceTrigger", "🎤 PARTIAL TRIGGER DETECTED: $triggerWord")
                onTriggerDetected()
            }
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        // Event
    }
}
