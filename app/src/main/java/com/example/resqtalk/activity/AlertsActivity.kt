package com.example.resqtalk.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.resqtalk.data.db.ResQtalkDatabase
import com.example.resqtalk.databinding.ActivityAlertsBinding
import kotlinx.coroutines.launch

class AlertsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlertsBinding
    private lateinit var database: ResQtalkDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = ResQtalkDatabase.getDatabase(this)

        setupUI()
        testDatabaseConnection()
        loadAlertStats()
    }

    private fun testDatabaseConnection() {
        lifecycleScope.launch {
            try {
                val allAlerts = database.alertHistoryDao().getAllAlerts()
                android.util.Log.d("AlertsActivity", "Database connection OK. Found ${allAlerts.size} alerts in database")
                allAlerts.forEach {
                    android.util.Log.d("AlertsActivity", "Alert: timestamp=${it.timestamp}, recipients=${it.recipientCount}, success=${it.successCount}, fail=${it.failCount}")
                }
            } catch (e: Exception) {
                android.util.Log.e("AlertsActivity", "Database connection ERROR: ${e.message}", e)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadAlertStats()
    }

    private fun loadAlertStats() {
        lifecycleScope.launch {
            try {
                val totalAlerts = database.alertHistoryDao().getTotalAlerts()
                val totalSuccessful = database.alertHistoryDao().getTotalSuccessful() ?: 0
                val totalFailed = database.alertHistoryDao().getTotalFailed() ?: 0

                android.util.Log.d("AlertsActivity", "Loading stats - Total: $totalAlerts, Success: $totalSuccessful, Failed: $totalFailed")

                binding.apply {
                    tvTotalAlerts.text = totalAlerts.toString()
                    tvSuccessful.text = totalSuccessful.toString()
                    tvFailed.text = totalFailed.toString()
                }
            } catch (e: Exception) {
                android.util.Log.e("AlertsActivity", "Error loading alert stats: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }

    private fun setupUI() {
        binding.apply {
            // Navigation
            navSos.setOnClickListener {
                startActivity(Intent(this@AlertsActivity, MainActivity::class.java))
                finish()
            }

            navContacts.setOnClickListener {
                startActivity(Intent(this@AlertsActivity, ContactsActivity::class.java))
            }

            navSettings.setOnClickListener {
                startActivity(Intent(this@AlertsActivity, SettingsActivity::class.java))
            }
        }
    }
}
