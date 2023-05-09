package com.example.digicard.data.repository

import androidx.lifecycle.LiveData
import com.example.digicard.data.database.ContactDao
import com.example.digicard.model.ContactModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


class DatabaseRepository @Inject constructor(private val favoriteDao:ContactDao)
{
    suspend fun saveContact(contactModel:ContactModel)
    {
        favoriteDao.saveProduct(contactModel)
    }

    fun getContact():LiveData<List<ContactModel>>
    {
       return favoriteDao.getAllFavoriteProducts()
    }
}