package com.example.resqtalk.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.resqtalk.data.entity.EmergencyContact

@Dao
interface EmergencyContactDao {
    @Insert
    suspend fun insertContact(contact: EmergencyContact): Long

    @Update
    suspend fun updateContact(contact: EmergencyContact)

    @Delete
    suspend fun deleteContact(contact: EmergencyContact)

    @Query("SELECT * FROM emergency_contacts")
    suspend fun getAllContacts(): List<EmergencyContact>

    @Query("SELECT * FROM emergency_contacts WHERE isEmergencyContact = 1")
    suspend fun getEmergencyContacts(): List<EmergencyContact>

    @Query("SELECT * FROM emergency_contacts WHERE id = :id")
    suspend fun getContactById(id: Int): EmergencyContact?

    @Query("DELETE FROM emergency_contacts")
    suspend fun deleteAllContacts()
}
