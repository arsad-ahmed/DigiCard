package com.example.digicard.util

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.digicard.R
import com.example.digicard.util.extention.loadImage

@BindingAdapter("imageUrl")
fun loadImage(imageView:ImageView, link: String)
{
    Log.d("MyTag", "loadImage: $link")
    if(link.isNotEmpty())
    {
        imageView.loadImage(link)
    }
    else
    {
        imageView.setImageResource(R.drawable.ic_profile)
    }
}