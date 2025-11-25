package com.example.resqtalk.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val phone: String,
    val isEmergencyContact: Boolean = true,
    val isTrackingEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
