package com.example.digicard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digicard.data.repository.AuthenticationRepository
import com.example.digicard.model.UserInfoModel
import com.example.digicard.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(private val authenticationRepository:AuthenticationRepository):
    ViewModel()
{
    private val _userInformation = MutableLiveData<Resource<UserInfoModel>>()
    val userInformationLiveData :LiveData<Resource<UserInfoModel>> =_userInformation


    init {
        getUserInformation()
    }

    private fun getUserInformation()
    {
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepository.getUserInformation(_userInformation)
        }
    }

}