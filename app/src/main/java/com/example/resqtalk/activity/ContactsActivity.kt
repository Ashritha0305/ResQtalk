package com.example.resqtalk.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.resqtalk.adapter.ContactAdapter
import com.example.resqtalk.data.db.ResQtalkDatabase
import com.example.resqtalk.data.entity.EmergencyContact
import com.example.resqtalk.databinding.ActivityContactsBinding
import kotlinx.coroutines.launch

class ContactsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactsBinding
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var database: ResQtalkDatabase
    private val contacts = mutableListOf<EmergencyContact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = ResQtalkDatabase.getDatabase(this)

        setupUI()
        loadContacts()
    }

    private fun setupUI() {
        binding.apply {
            // Setup RecyclerView
            rvContacts.layoutManager = LinearLayoutManager(this@ContactsActivity)
            contactAdapter = ContactAdapter(
                contacts,
                onEditClick = { editContact(it) },
                onDeleteClick = { deleteContact(it) },
                onToggleEmergency = { contact, isEmergency ->
                    updateContactStatus(contact, isEmergency)
                }
            )
            rvContacts.adapter = contactAdapter

            // Add contact button
            btnAddContact.setOnClickListener {
                showAddContactDialog()
            }

            // Import button
            if (btnImport != null) {
                btnImport.setOnClickListener {
                    Toast.makeText(this@ContactsActivity, "Import feature coming soon", Toast.LENGTH_SHORT).show()
                }
            }

            // Navigation
            navSos.setOnClickListener {
                startActivity(Intent(this@ContactsActivity, MainActivity::class.java))
                finish()
            }

            navAlerts.setOnClickListener {
                startActivity(Intent(this@ContactsActivity, AlertsActivity::class.java))
            }

            navSettings.setOnClickListener {
                startActivity(Intent(this@ContactsActivity, SettingsActivity::class.java))
            }
        }
    }

    private fun loadContacts() {
        lifecycleScope.launch {
            try {
                val loadedContacts = database.emergencyContactDao().getAllContacts()
                contacts.clear()
                contacts.addAll(loadedContacts)
                contactAdapter.notifyDataSetChanged()
                Log.d("ContactsActivity", "Loaded ${loadedContacts.size} contacts")
            } catch (e: Exception) {
                Log.e("ContactsActivity", "Error loading contacts: ${e.message}")
                Toast.makeText(this@ContactsActivity, "Error loading contacts", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddContactDialog() {
        val dialogBinding = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
            }
        }

        val etName = android.widget.EditText(this).apply {
            hint = "Name"
        }
        val etPhone = android.widget.EditText(this).apply {
            hint = "Phone Number"
            inputType = android.text.InputType.TYPE_CLASS_PHONE
        }

        dialogBinding.addView(etName)
        dialogBinding.addView(etPhone)

        AlertDialog.Builder(this)
            .setTitle("Add Emergency Contact")
            .setView(dialogBinding)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString()
                val phone = etPhone.text.toString()

                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    addContact(name, phone)
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addContact(name: String, phone: String) {
        lifecycleScope.launch {
            try {
                val contact = EmergencyContact(
                    name = name,
                    phone = phone,
                    isEmergencyContact = true
                )
                database.emergencyContactDao().insertContact(contact)
                Toast.makeText(this@ContactsActivity, "Contact added", Toast.LENGTH_SHORT).show()
                loadContacts()
            } catch (e: Exception) {
                Log.e("ContactsActivity", "Error adding contact: ${e.message}")
                Toast.makeText(this@ContactsActivity, "Error adding contact", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editContact(contact: EmergencyContact) {
        val dialogBinding = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
            }
        }

        val etName = android.widget.EditText(this).apply {
            setText(contact.name)
            hint = "Name"
        }
        val etPhone = android.widget.EditText(this).apply {
            setText(contact.phone)
            hint = "Phone Number"
            inputType = android.text.InputType.TYPE_CLASS_PHONE
        }

        dialogBinding.addView(etName)
        dialogBinding.addView(etPhone)

        AlertDialog.Builder(this)
            .setTitle("Edit Contact")
            .setView(dialogBinding)
            .setPositiveButton("Save") { _, _ ->
                val updatedName = etName.text.toString()
                val updatedPhone = etPhone.text.toString()

                if (updatedName.isNotEmpty() && updatedPhone.isNotEmpty()) {
                    updateContact(contact, updatedName, updatedPhone)
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateContact(contact: EmergencyContact, name: String, phone: String) {
        lifecycleScope.launch {
            try {
                val updatedContact = contact.copy(name = name, phone = phone)
                database.emergencyContactDao().updateContact(updatedContact)
                Toast.makeText(this@ContactsActivity, "Contact updated", Toast.LENGTH_SHORT).show()
                loadContacts()
            } catch (e: Exception) {
                Log.e("ContactsActivity", "Error updating contact: ${e.message}")
                Toast.makeText(this@ContactsActivity, "Error updating contact", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteContact(contact: EmergencyContact) {
        AlertDialog.Builder(this)
            .setTitle("Delete Contact")
            .setMessage("Are you sure you want to delete ${contact.name}?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    try {
                        database.emergencyContactDao().deleteContact(contact)
                        Toast.makeText(this@ContactsActivity, "Contact deleted", Toast.LENGTH_SHORT).show()
                        loadContacts()
                    } catch (e: Exception) {
                        Log.e("ContactsActivity", "Error deleting contact: ${e.message}")
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateContactStatus(contact: EmergencyContact, isEmergency: Boolean) {
        lifecycleScope.launch {
            try {
                val updatedContact = contact.copy(isEmergencyContact = isEmergency)
                database.emergencyContactDao().updateContact(updatedContact)
                Log.d("ContactsActivity", "Contact status updated")
            } catch (e: Exception) {
                Log.e("ContactsActivity", "Error updating contact status: ${e.message}")
            }
        }
    }
}
