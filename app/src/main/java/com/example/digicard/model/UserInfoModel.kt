package com.example.digicard.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.net.InetSocketAddress

@Parcelize
data class UserInfoModel(
        val userUid: String = "",
        val userImage: String,
        val userName: String,
        val userEmail: String,
        val userPhone:String,
        val userGithub:String,
        val userSkills:String,
        val userCity:String,
        val userCountry:String) : Parcelable
{

        fun toMapWithoutImage(): MutableMap<String, Any>
        {
                val map = mutableMapOf<String, Any>()
                map["userUid"] = userUid
                map["userName"] = userName
                map["userEmail"] = userEmail
                map["userPhone"] = userPhone
                map["userGithub"]=userGithub
                map["userSkills"]=userSkills
                map["userCountry"] = userCountry
                return map
        }
}

fun UserInfoModel.toMap(): Map<String, Any>
{
        val map = mutableMapOf<String, Any>()
        map["userUid"] = userUid
        map["userImage"] = userImage
        map["userName"] = userName
        map["userEmail"] = userEmail
        map["userPhone"] = userPhone
        map["userGithub"]=userGithub
        map["userSkills"]=userSkills
        map["userCity"] = userCity
        map["userCountry"] = userCountry
        return map
}

fun convertMapToUserInfoModel(map: Map<String, Any>): UserInfoModel
{
        return UserInfoModel(
                map["userUid"].toString(),
                map["userImage"].toString(),
                map["userName"].toString(),
                map["userEmail"].toString(),
                map["userPhone"].toString(),
                map["userGithub"].toString(),
                map["userSkills"].toString(),
                map["userCity"].toString(),
                map["userCountry"].toString()
                            )
}
