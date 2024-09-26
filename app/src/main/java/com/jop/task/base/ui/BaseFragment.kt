package com.jop.task.base.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.jop.task.R

open class BaseFragment: Fragment() {
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var alertDialog: AlertDialog
    private var dialog: AlertDialog.Builder? = null
    private var inflater: LayoutInflater? = null
    private var dialogView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(requireContext())
    }

    fun showLoadingDialog(){
        loadingDialog.showDialog()
    }

    fun hideLoadingDialog(){
        loadingDialog.hideDialog()
    }

    fun showFailedToast(text: String) {
        CustomSnackbar.make(requireActivity().findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT,
            R.color.orange,
            R.drawable.ic_failed_snackbar
        )?.show()
    }

    fun showSuccessToast(text: String) {
        CustomSnackbar.make(requireActivity().findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT,
            R.color.green_success,
            R.drawable.ic_success_snackbar,
        )?.show()
    }

    fun setupPermissionBL(): Boolean{
        val coarse = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
        val fine = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        return if (coarse != PackageManager.PERMISSION_GRANTED && fine != PackageManager.PERMISSION_GRANTED) {
            makeRequestBL()
            false
        }else{
            true
        }
    }

    private fun makeRequestBL() {
        requestPermissions(
                arrayOf(Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION),
                1003)
    }

    fun setupPermissionsFile(): Boolean {
        val write = ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val read = ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        val camera = ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.CAMERA)
        return if (write != PackageManager.PERMISSION_GRANTED && read != PackageManager.PERMISSION_GRANTED && camera != PackageManager.PERMISSION_GRANTED) {
            makeRequestFile()
            false
        }else{
            true
        }
    }

    private fun makeRequestFile() {
        requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                ),
                1002)
    }

    fun openApplicationSetting(){
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireActivity().packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun showCustomDialog(tittle: String, content: String, btnConfirmText: String, btnDismissText: String, btnConfirmListener: View.OnClickListener, btnDismissListener: View.OnClickListener){
        dialog = AlertDialog.Builder(requireActivity())
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