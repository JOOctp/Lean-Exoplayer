package com.jop.task.view.home.fragment

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.jop.task.R
import com.jop.task.base.ui.BaseFragment
import com.jop.task.data.model.Todo
import com.jop.task.data.model.User
import com.jop.task.data.viewModel.TaskViewModel
import com.jop.task.data.viewModel.UserViewModel
import com.jop.task.data.viewModel.factory.TaskViewModelFactory
import com.jop.task.data.viewModel.factory.UserViewModelFactory
import com.jop.task.databinding.FragmentAccountBinding
import com.jop.task.databinding.FragmentHomeBinding
import com.jop.task.databinding.FragmentTaskBinding
import com.jop.task.util.ImageResource
import com.jop.task.view.adapter.ParentTaskAdapter
import com.jop.task.view.login.LoginActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class AccountFragment : BaseFragment(), KodeinAware {
    override val kodein by kodein()
    private lateinit var binding: FragmentAccountBinding
    private lateinit var userVM: UserViewModel
    private val userVMF: UserViewModelFactory by instance()
    private var fileUri: Uri? = null
    private var user: User? = null
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val startForImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                data?.data!!.let {
                    val cr = requireActivity().contentResolver
                    val fd = cr.openAssetFileDescriptor(it, "r")
                    val fs = fd?.length

                    if(fs!! <= 2097152){
                        fileUri = it
                        val bitmap = BitmapFactory.decodeFile(it.path)
                        ImageResource.clearGlide(binding.ivLogo)
                        ImageResource.setupBitmapToImageView(bitmap, binding.ivLogo)
                    } else {
                        showFailedToast("Logo maksimal 2 MB")
                    }
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                showFailedToast(ImagePicker.getError(data))
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)

        userVM = ViewModelProvider(requireActivity(), userVMF).get(UserViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            btnLogout.setOnClickListener {
                showCustomDialog(
                    tittle = "Tunggu dulu",
                    content = "Apakah kamu ingin keluar ?",
                    btnConfirmText = "Ya, keluar",
                    btnDismissText = "Tidak",
                    btnConfirmListener = {
                        hideAlertDialog()
                        firebaseAuth.signOut()
                        startActivity(LoginActivity.newInstance(requireContext()))
                        requireActivity().finish()
                    },
                    btnDismissListener = {
                        hideAlertDialog()
                    }
                )
            }

            btnUpdate.setOnClickListener {
                val byteArrayPhoto = if(fileUri != null) requireActivity().contentResolver.openInputStream(fileUri!!)?.readBytes() ?: user!!.photo else user!!.photo
                userVM.updateUser(etUsername.text.toString(), byteArrayPhoto)
                showSuccessToast("Berhasil update data user")
            }

            btnSelectPhoto.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                        toGallery()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        toGallery()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun toGallery(){
        ImagePicker.with(this)
            .galleryOnly()
            .cropSquare()
            .compress(1024)
            .galleryMimeTypes(
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            ).createIntent { startForImageResult.launch(it) }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            toGallery()
        } else {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)) || (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU && ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE))) {
                showFailedToast("Izinkan aplikasi mengakses galeri")
            } else {
                openApplicationSetting()
            }
        }
    }

    override fun onStart() {
        userVM.getDetailUser().observe(requireActivity()){ data ->
            if(user == null){
                user = data[0]

                binding.etUsername.setText(user!!.name)
                binding.etEmailUser.setText(user!!.email)

                val bitmap = BitmapFactory.decodeByteArray(user!!.photo,0, user!!.photo.size)
                if(bitmap != null) ImageResource.setupBitmapToImageView(bitmap, binding.ivLogo)
            }
        }
        super.onStart()
    }
}