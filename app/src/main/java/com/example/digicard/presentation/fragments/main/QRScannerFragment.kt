package com.example.digicard.presentation.fragments.main

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.digicard.R
import com.example.digicard.databinding.FragmentQRScannerBinding
import com.example.digicard.model.ContactModel
import com.example.digicard.util.LOADING_ANNOTATION
import com.example.digicard.util.extention.*
import com.example.digicard.viewmodel.ContactViewModel
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named
import kotlin.properties.Delegates


@AndroidEntryPoint
class QRScannerFragment:Fragment(),PermissionListener
{
    private lateinit var binding:FragmentQRScannerBinding
    private val contactViewModel by activityViewModels<ContactViewModel>()

    private lateinit var barcodeView:DecoratedBarcodeView
    private lateinit var captureManager:CaptureManager
    private var isPermissionGranted = false

    private var input=StringBuffer()
    private var lastText:String?=null
    private lateinit var codeText:List<String>
    private var isAppQR =false

    @Inject
    @Named(LOADING_ANNOTATION)
    lateinit var loadingDialog:Dialog

    private val barcodeCallback = object :BarcodeCallback
    {
        override fun barcodeResult(result: BarcodeResult?)
        {
            if(result != null)
            {
                if ((result.text == null) || (result.text == lastText))
                {
                    return
                }
                lastText = result.text
                addContact(lastText!!)
                updateUI()

            }
            barcodeView.pause()

        }

        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
    }

    override fun onCreate(savedInstanceState:Bundle?)
    {
        super.onCreate(savedInstanceState)
        loadingDialog.hide()
        checkPermissions()
    }

    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?, savedInstanceState:Bundle?):View?
    {
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_q_r_scanner, container, false)
        barcodeView=binding.surfaceView
        binding.fragment=this
        codeText=arrayListOf()
        return binding.root
    }

    override fun onViewCreated(view:View, savedInstanceState:Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        captureManager = CaptureManager(requireActivity(), barcodeView).apply {
            initializeFromIntent(requireActivity().intent, savedInstanceState)
            decode()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPermissionGranted) {
            barcodeView.resume()
            barcodeView.decodeContinuous(barcodeCallback)
        }
    }

    override fun onPause() {
        super.onPause()
        if (isPermissionGranted) {
            barcodeView.pause()
        }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        captureManager.onSaveInstanceState(outState)
    }

    private fun updateUI()
    {
        binding.apply {
            surfaceView.hide()
            cardView1.show()
            qrResult.show()
            userImage.show()
            saveAsContact.show()
        }
    }


    fun saveToContact()
    {
        if(isAppQR)
        {
            contactViewModel.addContact(ContactModel(codeText[2],codeText[0],input.toString()))
        }

        else
        {
            contactViewModel.addContact(ContactModel(input.toString(),"",input.toString()))
        }
        Toast.makeText(requireContext(), "Contact saved successfully", Toast.LENGTH_SHORT).show()
        closeFragment()
    }

    private fun addContact(text:String)
    {
        val regexPattern = Regex("^https://firebasestorage\\.googleapis\\.com.*")
        codeText=text.split("\n")
        if (regexPattern.matches(codeText[0]))
        {
            isAppQR=true

            for(i in 1 until codeText.size)
            {
                input.append(codeText[i])
                if(i<codeText.size-1)
                {
                    input.append("\n")
                }
            }
            binding.userImage.loadImage(codeText[0])
        }

        else
        {
            isAppQR=false
            input.append(codeText).toString()
        }

        binding.qrResult.text=input.toString()
    }


    private fun checkPermissions()
    {
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.CAMERA).withListener(this).check()
    }

    override fun onPermissionGranted(p0:PermissionGrantedResponse?)
    {
        isPermissionGranted=true
    }

    override fun onPermissionDenied(p0:PermissionDeniedResponse?)
    {
        showToast("Camera permission is required to use QR scanner")
        isPermissionGranted=false
    }

    override fun onPermissionRationaleShouldBeShown(p0:PermissionRequest?, p1:PermissionToken?)
    {
        p1?.continuePermissionRequest()
        isPermissionGranted=false
    }

}