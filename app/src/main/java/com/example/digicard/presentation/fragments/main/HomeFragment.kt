package com.example.digicard.presentation.fragments.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.digicard.R
import com.example.digicard.databinding.FragmentHomeBinding
import com.example.digicard.model.UserInfoModel
import com.example.digicard.util.LOADING_ANNOTATION
import com.example.digicard.util.Resource
import com.example.digicard.util.extention.showToast
import com.example.digicard.viewmodel.UserInfoViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class HomeFragment : Fragment()
{

    private val userInfoViewModel by activityViewModels<UserInfoViewModel>()
    private var userInfoModel: UserInfoModel? = null

    private lateinit var binding:FragmentHomeBinding
    private var bitmap:Bitmap?=null

    @Inject
    @Named(LOADING_ANNOTATION)
    lateinit var loadingDialog:Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false)
        binding.fragment=this
        observeLivedata()
        return binding.root
    }

    private fun observeLivedata()
    {
        userInfoViewModel.userInformationLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is Resource.Success ->
                {
                    loadingDialog.hide()
                    userInfoModel=it.data
                    binding.userInfo=userInfoModel
                    bitmap=userInfoModel?.let {it1 -> generateQRCode(it1)}
                    binding.qrScanner.setImageBitmap(bitmap)
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

    fun navigateToContactsFragment()
    {
        val action=HomeFragmentDirections.actionHomeFragmentToAllContactsFragment()
        findNavController().navigate(action)
    }

    fun navigateToQRScannerFragment()
    {
        val action=HomeFragmentDirections.actionHomeFragmentToQRScannerFragment()
        findNavController().navigate(action)
    }

    fun navigateToProfileFragment()
    {
        val action=HomeFragmentDirections.actionHomeFragmentToProfileFragment(false)
        findNavController().navigate(action)
    }

    private fun generateQRCode(userInfoModel:UserInfoModel): Bitmap?
    {
        val input="${userInfoModel.userImage}\n" +
                "${userInfoModel.userName}\n" +
                "${userInfoModel.userEmail}\n" +
                "${userInfoModel.userPhone}\n" +
                "${userInfoModel.userGithub}\n" +
                "${userInfoModel.userSkills}\n" +
                "${userInfoModel.userCity},${userInfoModel.userCountry}"

        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(input, BarcodeFormat.QR_CODE, 200, 200)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width)
            {
                for (y in 0 until height)
                {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            return bmp
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return null
    }

    fun sendQrCode()
    {
        val bitmapPath = MediaStore.Images.Media.insertImage(requireContext().contentResolver, bitmap, "${System.currentTimeMillis()}.jpg", null)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        val bitmapUri = Uri.parse(bitmapPath)
        shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
        requireContext().startActivity(Intent.createChooser(shareIntent, "Share Bitmap"))
    }

}