package com.example.digicard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digicard.data.repository.DatabaseRepository
import com.example.digicard.model.ContactModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(private val databaseRepository:DatabaseRepository):ViewModel()
{
    private lateinit var _contactLiveData:LiveData<List<ContactModel>>
    val contactLiveData get() = _contactLiveData

     fun getContacts()
    {
        viewModelScope.launch(Dispatchers.IO) {
            _contactLiveData = databaseRepository.getContact()
        }
    }

    fun addContact(contactModel:ContactModel)
    {
        viewModelScope.launch (Dispatchers.IO){
            databaseRepository.saveContact(contactModel)
        }
    }
}