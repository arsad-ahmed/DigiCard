package com.example.digicard.presentation.fragments.authentication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.digicard.R
import com.example.digicard.databinding.FragmentLoginBinding
import com.example.digicard.util.LOADING_ANNOTATION
import com.example.digicard.util.Resource
import com.example.digicard.util.extention.hide
import com.example.digicard.util.extention.show
import com.example.digicard.util.extention.showToast
import com.example.digicard.viewmodel.AuthenticationViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class LoginFragment:Fragment()
{
    private lateinit var binding:FragmentLoginBinding
    private val authViewModel by viewModels<AuthenticationViewModel>()

    private lateinit var googleSignInClient:GoogleSignInClient
    private lateinit var googleSignInOptions:GoogleSignInOptions

    @Inject
    lateinit var firebaseAuth:FirebaseAuth

    @Inject
    @Named(LOADING_ANNOTATION)
    lateinit var loadingDialog:Dialog

    override fun onCreate(savedInstanceState:Bundle?)
    {
        super.onCreate(savedInstanceState)
        setGoogleSignIn()
        checkIfUserLoggedIn()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        observeLiveData()

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.fragment = this
        binding.btSignIn.setOnClickListener(View.OnClickListener {signIn()})
        return binding.root
    }

    private fun setGoogleSignIn()
    {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(),googleSignInOptions)
    }

    private fun signIn()
    {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInResultLauncher.launch(signInIntent)
    }


    private val googleSignInResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        result ->
        if(result.resultCode== Activity.RESULT_OK)
        {
            val task=GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }

    }

    private fun handleSignInResult(result:Task<GoogleSignInAccount>) {
        if (result.isSuccessful)
        {
            val account=result.result
            firebaseAuthWithGoogle(account)
        }
        else
        {
            showToast("Authentication failed")
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount)
    {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful)
                {
                    showToast("login successfully")
                    firebaseAuth.currentUser?.let {it.email?.let {it1 -> authViewModel.uploadUserInformation(it.uid,null,"", it1,"","","","","")}}
                    val action=LoginFragmentDirections.actionLoginFragmentToProfileFragment(true)
                    findNavController().navigate(action)

                }
                else
                {
                    showToast("Authentication failed")
                }
            }
    }


    private fun checkIfUserLoggedIn()
    {
        val isLoggedIn = authViewModel.isUserLoggedIn()
        if(isLoggedIn)
        {
            navigateToHomeFragment()
        }
    }

    private fun navigateToHomeFragment()
    {
        val action=LoginFragmentDirections.actionLoginFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun observeLiveData()
    {
        authViewModel.signInStatusLiveData.observe(viewLifecycleOwner) {
            when(it)
            {
                is Resource.Loading ->
                {
                    loadingDialog.show()
                }
                is Resource.Success ->
                {
                    loadingDialog.hide()
                    showToast("Login Successfully")
                    navigateToHomeFragment()
                }
                is Resource.Error ->
                {
                    loadingDialog.hide()
                    showToast(it.message.toString())
                }
            }
        }
    }


    fun signInUser()
    {
        checkInput()
    }

    fun signUpUser()
    {
        val action=LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
        findNavController().navigate(action)
    }

    @SuppressLint("SetTextI18n")
    private fun checkInput()
    {
        val email=binding.loginEmailET.text.toString().trim()
        val pass=binding.LoginPassEt.text.toString().trim()

        if (email.isEmpty())
        {
            binding.emailError.show()
            binding.emailError.text = "Email Can't be Empty"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            binding.emailError.show()
            binding.emailError.text = "Enter Valid Email"
            return
        }
        if(pass.isEmpty())
        {
            binding.passwordError.show()
            binding.passwordError.text= "Password Can't be Empty"
            return
        }

        if (pass.isNotEmpty() and email.isNotEmpty())
        {
            binding.emailError.hide()
            binding.passwordError.hide()

            val emailId=binding.loginEmailET.text.toString().trim()
            val password=binding.LoginPassEt.text.toString().trim()
            authViewModel.userLogin(emailId,password)
        }
    }
}