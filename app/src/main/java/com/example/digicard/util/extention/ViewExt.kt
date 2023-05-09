package com.example.digicard.util.extention

import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}
fun View.showWithAnimate(anim: Int){
    show()
    animation = AnimationUtils.loadAnimation(context, anim).apply {
        start()
    }
}

fun ImageView.loadImage(link: String){
    Glide.with(context).load(link).into(this)
}
