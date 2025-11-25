package com.example.resqtalk.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert_history")
data class AlertHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long,
    val message: String,
    val recipientCount: Int,
    val successCount: Int,
    val failCount: Int,
    val location: String
)
