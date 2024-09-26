package com.jop.task.base.ui

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.jop.task.R

class LoadingDialog(private var context: Context) {
    private lateinit var dialog: Dialog

    fun showDialog() {
        dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_loading_layout)

        dialog.show()
    }

    fun hideDialog() {
        if(::dialog.isInitialized) dialog.dismiss()
    }
}