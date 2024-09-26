package com.jop.task.base.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.jop.task.R

open class BaseActivity: AppCompatActivity() {
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var alertDialog: AlertDialog
    private var dialog: AlertDialog.Builder? = null
    private var inflater: LayoutInflater? = null
    private var dialogView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadingDialog = LoadingDialog(this)
    }

    fun showLoadingDialog(){
        loadingDialog.showDialog()
    }

    fun hideLoadingDialog(){
        loadingDialog.hideDialog()
    }

    fun showFailedToast(text: String, isShort: Boolean = false) {
        CustomSnackbar.make(
            findViewById(android.R.id.content),
            text,
            if (isShort) Snackbar.LENGTH_SHORT else Snackbar.LENGTH_LONG,
            R.color.orange,
            R.drawable.ic_failed_snackbar
        )?.show()
    }

    fun showSuccessToast(text: String, isShort: Boolean = true) {
        CustomSnackbar.make(
            findViewById(android.R.id.content),
            text,
            if (isShort) Snackbar.LENGTH_SHORT else Snackbar.LENGTH_LONG,
            R.color.green_success,
            R.drawable.ic_success_snackbar,
        )?.show()
    }

    fun openApplicationSetting(){
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", this.packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun showCustomDialog(tittle: String, content: String, btnConfirmText: String, btnDismissText: String, btnConfirmListener: View.OnClickListener, btnDismissListener: View.OnClickListener){
        dialog = AlertDialog.Builder(this)
        inflater = layoutInflater
        dialogView = inflater!!.inflate(R.layout.custom_layout_dialog, null)
        dialog!!.setView(dialogView)
        dialog!!.setCancelable(false)
        alertDialog = dialog!!.create()

        val tvTittle: TextView = dialogView!!.findViewById(R.id.tv_title)
        val tvContent: TextView = dialogView!!.findViewById(R.id.tv_sub_title)
        val btnOke: Button = dialogView!!.findViewById(R.id.btn_oke)
        val btnCancel: Button = dialogView!!.findViewById(R.id.btn_cancel)

        tvTittle.text = tittle
        tvContent.text = content

        btnOke.text = btnConfirmText
        btnCancel.text = btnDismissText

        if(btnDismissText.isEmpty()) btnCancel.visibility = View.GONE

        btnOke.setOnClickListener(btnConfirmListener)
        btnCancel.setOnClickListener(btnDismissListener)

        alertDialog.show()
    }

    fun hideAlertDialog(){
        alertDialog.dismiss()
    }
}