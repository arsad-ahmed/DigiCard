package com.example.digicard.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.digicard.model.ContactModel


@Dao
interface ContactDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProduct(contactModel:ContactModel)

    @Query("SELECT * FROM contactModel")
    fun getAllFavoriteProducts():LiveData<List<ContactModel>>
}