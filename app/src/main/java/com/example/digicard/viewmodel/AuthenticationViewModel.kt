package com.example.digicard.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digicard.data.repository.AuthenticationRepository
import com.example.digicard.util.Resource
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(private val authenticationRepository:AuthenticationRepository) : ViewModel()
{

    private val _signInStatusLiveData=MutableLiveData<Resource<Unit?>>()
    val signInStatusLiveData:LiveData<Resource<Unit?>>
        get()=_signInStatusLiveData


    private val _signUpStatusLiveData=MutableLiveData<Resource<FirebaseUser?>>()
    val signUpStatusLiveData:LiveData<Resource<FirebaseUser?>>
        get()=_signUpStatusLiveData


    private val _userInfoLiveData = MutableLiveData<Resource<String>>()
    val userInfoLiveData: LiveData<Resource<String>>
    get() = _userInfoLiveData


    fun isUserLoggedIn(): Boolean = authenticationRepository.isUserLoggedIn()

    fun userLogin(email:String, password:String)
    {
        _signInStatusLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO){
           _signInStatusLiveData.postValue(authenticationRepository.userLogin(email,password))
        }
    }

    fun userSignUp(email:String, password:String)
    {
        _signUpStatusLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO){
            _signUpStatusLiveData.postValue(authenticationRepository.userSignUp(email,password))
        }
    }

    fun uploadUserInformation(userUid: String,imageUri: Uri?,userName:String,userEmail: String,userPhone:String,userGithub:String,userSkills:String,userCity:String,userCountry:String)
    {
        _userInfoLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            _userInfoLiveData.postValue(authenticationRepository.uploadUserInformation(userUid,imageUri,userName,userEmail,userPhone,userGithub,userSkills,userCity,userCountry))
        }

    }


}