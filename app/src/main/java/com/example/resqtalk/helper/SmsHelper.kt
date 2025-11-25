package com.example.resqtalk.helper

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.getSystemService

class SmsHelper(private val context: Context) {

    fun sendSMS(phoneNumber: String, message: String): Boolean {
        return try {
            val smsManager = context.getSystemService<SmsManager>()
            if (smsManager != null) {
                val parts = smsManager.divideMessage(message)
                val sentIntents = ArrayList<PendingIntent>()
                
                for (part in parts) {
                    sentIntents.add(
                        PendingIntent.getBroadcast(
                            context,
                            0,
                            Intent("SMS_SENT"),
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                }
                
                smsManager.sendMultipartTextMessage(
                    phoneNumber,
                    null,
                    parts,
                    sentIntents,
                    null
                )
                Log.d("SmsHelper", "SMS sent to $phoneNumber")
                true
            } else {
                Log.e("SmsHelper", "SmsManager is null")
                false
            }
        } catch (e: Exception) {
            Log.e("SmsHelper", "Error sending SMS to $phoneNumber: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    data class SmsResult(
        val successCount: Int,
        val failCount: Int
    )

    fun sendSOStoMultipleContacts(phoneNumbers: List<String>, message: String): SmsResult {
        var successCount = 0
        var failCount = 0
        for (phoneNumber in phoneNumbers) {
            if (sendSMS(phoneNumber, message)) {
                successCount++
            } else {
                failCount++
            }
        }
        return SmsResult(successCount, failCount)
    }
}
