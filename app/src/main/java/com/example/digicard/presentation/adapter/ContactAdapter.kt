package com.example.digicard.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.digicard.BR
import com.example.digicard.R
import com.example.digicard.model.ContactModel
import javax.inject.Inject

class ContactAdapter @Inject constructor(): RecyclerView.Adapter<ContactAdapter.ContactViewHolder>()
{

    private val contactList = mutableListOf<ContactModel>()
    private val contactSaveList=mutableListOf<ContactModel>()

    fun addProducts(list: List<ContactModel>)
    {
        contactList.clear()
        contactSaveList.clear()
        contactList.addAll(list)
        contactSaveList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int):ContactViewHolder
    {
        return ContactViewHolder(
                DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.contacts_rv,
                parent, false))
    }

    override fun getItemCount():Int
    {
       return contactList.size
    }

    override fun onBindViewHolder(holder:ContactViewHolder, position:Int)
    {
        holder.bind(contactList[position])
    }

    inner class ContactViewHolder(private val binding:ViewDataBinding) :RecyclerView.ViewHolder(binding.root)
    {

        fun bind(contactModel: ContactModel)
        {
            binding.setVariable(BR.contactModel, contactModel)
        }
    }

}