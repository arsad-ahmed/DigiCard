package com.example.digicard.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.digicard.R
import com.example.digicard.model.UserInfoModel
import com.example.digicard.model.convertMapToUserInfoModel
import com.example.digicard.model.toMap
import com.example.digicard.util.Resource
import com.example.digicard.util.USERS_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ViewModelScoped
class AuthenticationRepository @Inject constructor(private val firebaseAuth:FirebaseAuth,
                                                   private val firebaseStorage:FirebaseStorage,
                                                   private val firebaseFirestore: FirebaseFirestore,
                                                   @ApplicationContext private val context:Context)
{

    private val userUid by lazy { firebaseAuth.uid!! }
    private val firebaseUserCollection by lazy { firebaseFirestore.collection(USERS_COLLECTION) }


    fun isUserLoggedIn(): Boolean
    {
        val user = firebaseAuth.currentUser
        return user != null

    }

    suspend fun userLogin(email:String, password:String):Resource<Unit?>
    {
       return try
        {
           firebaseAuth.signInWithEmailAndPassword(email,password).await()
            Resource.Success(null)
        }
        catch(e:Exception)
        {
            Resource.Error(e.message.toString())
        }
    }
    suspend fun userSignUp(email:String,password:String):Resource<FirebaseUser?>
    {
        return try
        {
            firebaseAuth.createUserWithEmailAndPassword(email,password).await()
            Resource.Success(firebaseAuth.currentUser)
        }
        catch(e:Exception)
        {
            Resource.Error(e.message.toString())
        }
    }

    suspend fun uploadUserInformation(userUid:String,imageUri: Uri?,userName:String,userEmail: String,userPhone:String,userGithub:String,userSkills:String,userCity:String,userCountry:String): Resource<String>
    {
        return try
        {
            var accountStatusMessage = context.getString(R.string.accountUpdatedSuccessfully)
            if (imageUri != null)
            {
                val uploadedImagePath = uploadUserImage(imageUri)
                val userInfoModel =UserInfoModel(userUid,uploadedImagePath,userName,userEmail,userPhone,userGithub,userSkills,userCity,userCountry)
                firebaseUserCollection.document(userUid).update(userInfoModel.toMap()).await()
            }
            else
            {
                val userInfoModel =UserInfoModel(userUid,"",userName,userEmail,userPhone,userGithub,userSkills,userCity,userCountry)
                firebaseUserCollection.document(userUid).set(userInfoModel.toMapWithoutImage()).await()
                accountStatusMessage = context.getString(R.string.accountUpdatedSuccessfully)
            }
            Resource.Success(accountStatusMessage)
        }
        catch (e: Exception)
        {
            Log.d("MyTag", "uploadUserInformation: $e")
            Resource.Error(e.toString())
        }
    }

    private suspend fun uploadUserImage(imageUri:Uri): String
    {
        val uploadingResult =firebaseStorage.reference
            .child("${USERS_COLLECTION}/${System.currentTimeMillis()}.jpg")
            .putFile(imageUri).await()
        return uploadingResult.metadata?.reference?.downloadUrl?.await().toString()
    }



    fun getUserInformation(userInfoLiveData: MutableLiveData<Resource<UserInfoModel>>)
    {
        firebaseFirestore.collection(USERS_COLLECTION).document(userUid)
            .addSnapshotListener { value, _ ->
                if (value == null)
                {
                    userInfoLiveData.postValue(Resource.Error(context.getString(R.string.errorMessage)))
                }
                else
                {
                    val userInfoModel = convertMapToUserInfoModel(value.data!!)
                    userInfoLiveData.postValue(Resource.Success(userInfoModel))
                }
            }
    }


}
