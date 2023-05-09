package com.example.digicard.presentation.fragments.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.airbnb.lottie.LottieDrawable
import com.example.digicard.R
import com.example.digicard.databinding.FragmentAllContactsBinding
import com.example.digicard.databinding.FragmentAllContactsBindingImpl
import com.example.digicard.presentation.adapter.ContactAdapter
import com.example.digicard.util.extention.hide
import com.example.digicard.util.extention.show
import com.example.digicard.viewmodel.ContactViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AllContactsFragment:Fragment()
{
    private val contactViewModel by activityViewModels<ContactViewModel>()
    private lateinit var binding:FragmentAllContactsBinding

    @Inject
    lateinit var contactAdapter: ContactAdapter

    override fun onCreate(savedInstanceState:Bundle?)
    {
        super.onCreate(savedInstanceState)
        contactViewModel.getContacts()
    }

    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?, savedInstanceState:Bundle?):View?
    {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_all_contacts,container,false)
        observeListener()
        setAdapter()
        return binding.root
    }

    private fun observeListener()
    {
        contactViewModel.contactLiveData.observe(viewLifecycleOwner) {
            if(it.isEmpty())
            {
               showAnimation()
            }
            else
            {
                pauseAnimation()
                contactAdapter.addProducts(it)
            }
        }
    }

    private fun setAdapter()
    {
        contactAdapter=ContactAdapter()
        binding.contactRecView.adapter=contactAdapter
    }

    private fun showAnimation()
    {
        binding.apply {
            animationContactPage.playAnimation()
            animationContactPage.repeatCount=LottieDrawable.INFINITE
            MyContactText.hide()
            contactRecView.hide()
            emptyContactLayout.show()
        }
    }

    private fun pauseAnimation()
    {
        binding.apply {
            animationContactPage.pauseAnimation()
            MyContactText.show()
            contactRecView.show()
            emptyContactLayout.hide()
        }
    }

}