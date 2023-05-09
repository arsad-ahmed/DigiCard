package com.example.digicard.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.digicard.model.ContactModel

@Database(entities = [ContactModel::class], version = 1)
abstract class ContactDatabase:RoomDatabase()
{
    abstract fun getDao():ContactDao
}