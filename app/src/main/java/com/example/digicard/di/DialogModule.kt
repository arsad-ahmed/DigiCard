package com.example.digicard.di


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import com.example.digicard.R
import com.example.digicard.util.DISPLAY_DIALOG
import com.example.digicard.util.LOADING_ANNOTATION
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import javax.inject.Named


@Module
@InstallIn(ActivityComponent::class)
class DialogModule
{
    @Inject
    @Provides
    @Named(LOADING_ANNOTATION)
    fun provideLoadingDialog(@ActivityContext context:Context):Dialog
    {
        val dialog = Dialog(context)

        val builder=AlertDialog.Builder(context)
        builder.setView(dialog.layoutInflater.inflate(R.layout.custome_dialogue, null))
        builder.setCancelable(false)
        return builder.create()
    }

    @ActivityScoped
    @Provides
    @Named(DISPLAY_DIALOG)
    fun provideDisplayAlert(@ActivityContext context: Context) = AlertDialog.Builder(context).let { builder ->
        builder.setPositiveButton(context.getString(R.string.ok), null)
        builder.create()
    }
}