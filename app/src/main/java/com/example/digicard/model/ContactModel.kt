package com.example.digicard.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "contactModel")
@Parcelize
data class ContactModel(
    @PrimaryKey()
    val id:String,
    val image:String,
    val text: String):Parcelable
