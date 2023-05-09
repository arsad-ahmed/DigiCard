package com.example.digicard.presentation.fragments.authentication

import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.digicard.R
import com.example.digicard.databinding.FragmentSignUpBinding
import com.example.digicard.util.LOADING_ANNOTATION
import com.example.digicard.util.Resource
import com.example.digicard.util.extention.showToast
import com.example.digicard.viewmodel.AuthenticationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SignUpFragment:Fragment()
{

    private lateinit var binding:FragmentSignUpBinding
    private val authViewModel:AuthenticationViewModel by activityViewModels()

    private lateinit var emailET:AppCompatEditText
    private lateinit var passET:AppCompatEditText
    private lateinit var cnfPassET:AppCompatEditText


    @Inject
    @Named(LOADING_ANNOTATION)
    lateinit var loadingDialog:Dialog


    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?, savedInstanceState:Bundle?):View?
    {
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        binding.apply {
            emailET=emailEt
            passET=passwordEt
            cnfPassET=cnfPassEt
            fragment=this@SignUpFragment

        }
        observeLiveData()
        return binding.root
    }

    fun userSignUp()
    {
        checkInput()
    }

    private fun checkInput()
    {
        val email=emailET.text.toString().trim()
        val password=passET.text.toString().trim()
        val cnfPassword=cnfPassET.text.toString().trim()

        if (email.isEmpty())
        {
            showToast("Email can't empty!")
            return
        }

        if (!email.matches(Patterns.EMAIL_ADDRESS.toRegex()))
        {
            showToast("Enter Valid Email")
            return
        }
        if(password.isEmpty())
        {
            showToast("Password can't empty!")
            return
        }
        if (password != cnfPassword)
        {
            showToast("Password not Match")
            return
        }

        authViewModel.userSignUp(email,password)
    }

    private fun observeLiveData()
    {

        authViewModel.signUpStatusLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is Resource.Loading ->
                {
                    loadingDialog.show()
                }
                is Resource.Success ->
                {
                    val email=emailET.text.toString().trim()
                    loadingDialog.hide()
                    showToast("going to profile page")

                    it.data?.let {it1 -> authViewModel.uploadUserInformation(it1.uid,null,"",email,"","","","","")}
                    val action=SignUpFragmentDirections.actionSignUpFragmentToProfileFragment(true)
                    findNavController().navigate(action)

                }
                is Resource.Error ->
                {
                    loadingDialog.hide()
                    showToast(it.message.toString())
                }
            }
        }

    }
}