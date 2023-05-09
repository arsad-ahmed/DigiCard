package com.example.digicard.presentation.fragments.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.digicard.R
import com.example.digicard.databinding.FragmentProfileBinding
import com.example.digicard.model.UserInfoModel
import com.example.digicard.util.CACHE_DIR
import com.example.digicard.util.LOADING_ANNOTATION
import com.example.digicard.util.Resource
import com.example.digicard.util.extention.*
import com.example.digicard.viewmodel.AuthenticationViewModel
import com.example.digicard.viewmodel.UserInfoViewModel
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class ProfileFragment:Fragment(),PermissionListener
{
    private lateinit var binding:FragmentProfileBinding
    private val authViewModel by activityViewModels<AuthenticationViewModel>()
    private val userInfoViewModel by activityViewModels<UserInfoViewModel>()

    private val args by navArgs<ProfileFragmentArgs>()
    private val mIsFirstTime by lazy {args.isFirstTime}

    private var userInfoModel: UserInfoModel? = null
    private var mImageUri: Uri? = null

    @Inject
    lateinit var firebaseAuth:FirebaseAuth

    @Inject
    @Named(LOADING_ANNOTATION)
    lateinit var loadingDialog:Dialog

    override fun onCreate(savedInstanceState:Bundle?)
    {
        super.onCreate(savedInstanceState)
        loadingDialog.hide()
    }


    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?,savedInstanceState:Bundle?):View?
    {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_profile, container, false)
        binding.fragment=this
        observeUser()
        return binding.root
    }

    override fun onResume()
    {
        super.onResume()
        showUserImage()
    }

    private fun observeUser()
    {
        userInfoViewModel.userInformationLiveData.observe(viewLifecycleOwner) {
            when(it)
            {
                is Resource.Success ->
                {
                    loadingDialog.hide()
                    userInfoModel=it.data
                    binding.userInfo=userInfoModel
                }
                is Resource.Error ->
                {
                    loadingDialog.hide()
                    showToast(it.message.toString())
                }
                is Resource.Loading ->
                {
                    loadingDialog.show()
                }
            }
        }
    }
    private fun observeLiveData()
    {
        authViewModel.userInfoLiveData.observe(viewLifecycleOwner) {info ->
            when(info)
            {
                is Resource.Loading ->
                {
                    loadingDialog.show()
                }

                is Resource.Success ->
                {
                    loadingDialog.hide()
                    showToast("profile set successfully")
                    val action=ProfileFragmentDirections.actionProfileFragmentToHomeFragment()
                    findNavController().navigate(action)
                }
                is Resource.Error ->
                {
                    loadingDialog.hide()
                    showToast(info.message.toString())
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    fun submitUserInfo()
    {
        val name=binding.userName.text.toString().trim()
        val phone=binding.phoneNumber.text.toString().trim()
        val github=binding.githubProfile.text.toString().trim()
        val skills=binding.skills.text.toString().trim()
        val city=binding.city.text.toString().trim()
        val country=binding.country.text.toString().trim()


        if (name.isEmpty())
        {
            showToast("Name can't empty!")
            return
        }
        if (phone.isEmpty())
        {
            showToast("Number can't empty!")
            return
        }
        if (github.isEmpty())
        {
            showToast("GitHub can't empty!")
            return
        }
        if (skills.isEmpty())
        {
            showToast("Skills can't empty!")
            return
        }
        if (city.isEmpty())
        {
            showToast("City can't empty!")
            return
        }
        if (country.isEmpty())
        {
            showToast("Country can't empty!")
            return
        }

        if(!mIsFirstTime && mImageUri==null)
        {
            showToast("Please select profile photo again")
            return
        }

        if(mImageUri == null)
        {
            showToast(getString(R.string.addUserImage))
            return
        }



        firebaseAuth.uid?.let {
            firebaseAuth.currentUser?.email?.let {it1 ->
            authViewModel.uploadUserInformation(it, mImageUri,name,it1,phone,github,skills,city,country)
        }}

        observeLiveData()

    }

    fun setProfilePhoto()
    {
        val popupMenu= PopupMenu(context,binding.userProfileImage)

        popupMenu.menuInflater.inflate(R.menu.profile_photo_menu,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {item ->
            when(item.itemId)
            {
                R.id.galleryMenu -> selectImageFromGallery()
                R.id.cameraMenu -> checkPermissions()

            }
            true
        }
        popupMenu.show()
    }

    private fun selectImageFromGallery()
    {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
    }

    private val resultLauncher =registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        result ->
        mImageUri = result.data?.data
    }


    private fun setImageUsingCamera()
    {
        val intent=Intent()
        intent.action=MediaStore.ACTION_IMAGE_CAPTURE
        cameraResultLauncher.launch(intent)

    }

    private val cameraResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        result ->
        if(result.resultCode== Activity.RESULT_OK)
        {
            val bitmap=result.data?.extras?.get("data") as Bitmap
            bitmapToImageUri(bitmap)
        }

    }

    private fun bitmapToImageUri(bitmap:Bitmap)
    {

        binding.userProfileImage.setImageBitmap(bitmap)
        val file = File(requireContext().cacheDir, CACHE_DIR)
        file.delete()
        file.createNewFile()
        val fileOutputStream = file.outputStream()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream)
        val bytearray = byteArrayOutputStream.toByteArray()
        fileOutputStream.write(bytearray)
        fileOutputStream.flush()
        fileOutputStream.close()
        byteArrayOutputStream.close()

        mImageUri = file.toUri()
    }

    private fun showUserImage()
    {
        if (mImageUri != null)
        {
            binding.userProfileImage.setImageURI(mImageUri)
        }
        else if(!mIsFirstTime)
        {
            binding.profile.text="Edit Profile"
            userInfoModel?.let {
                binding.userProfileImage.loadImage(it.userImage)
            }
        }
        else
        {
            binding.userProfileImage.setImageResource(R.drawable.ic_profile)
        }
    }


    private fun checkPermissions()
    {
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.CAMERA).withListener(this).check()
    }

    override fun onPermissionGranted(p0:PermissionGrantedResponse?)
    {
        setImageUsingCamera()
    }

    override fun onPermissionDenied(p0:PermissionDeniedResponse?)
    {
        showToast("Camera permission is required to take picture")

    }

    override fun onPermissionRationaleShouldBeShown(p0:PermissionRequest?, p1:PermissionToken?)
    {
        p1?.continuePermissionRequest()

    }

}