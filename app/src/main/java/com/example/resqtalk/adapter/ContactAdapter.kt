package com.example.resqtalk.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.resqtalk.data.entity.EmergencyContact
import com.example.resqtalk.databinding.ItemContactBinding

class ContactAdapter(
    private val contacts: MutableList<EmergencyContact>,
    private val onEditClick: (EmergencyContact) -> Unit,
    private val onDeleteClick: (EmergencyContact) -> Unit,
    private val onToggleEmergency: (EmergencyContact, Boolean) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: EmergencyContact) {
            binding.apply {
                tvContactName.text = contact.name
                tvContactPhone.text = contact.phone
                switchEmergency.isChecked = contact.isEmergencyContact

                switchEmergency.setOnCheckedChangeListener { _, isChecked ->
                    onToggleEmergency(contact, isChecked)
                }

                btnEdit.setOnClickListener {
                    onEditClick(contact)
                }

                btnDelete.setOnClickListener {
                    onDeleteClick(contact)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int = contacts.size

    fun updateContacts(newContacts: List<EmergencyContact>) {
        contacts.clear()
        contacts.addAll(newContacts)
        notifyDataSetChanged()
    }
}
