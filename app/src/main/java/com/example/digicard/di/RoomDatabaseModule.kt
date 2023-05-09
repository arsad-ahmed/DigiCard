package com.example.digicard.di

import android.content.Context
import androidx.room.Room
import com.example.digicard.data.database.ContactDatabase
import com.example.digicard.util.CONTACT_DATABASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomDatabaseModule
{
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context:Context) =
        Room.databaseBuilder(context, ContactDatabase::class.java, CONTACT_DATABASE).build()


    @Singleton
    @Provides
    fun provideYourDao(db: ContactDatabase) = db.getDao()
}