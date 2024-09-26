package com.jop.task.view.task

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import com.jop.task.R
import com.jop.task.base.ui.BaseActivity
import com.jop.task.data.model.Spinner
import com.jop.task.data.model.Todo
import com.jop.task.data.viewModel.ManageTaskViewModel
import com.jop.task.data.viewModel.factory.ManageTaskViewModelFactory
import com.jop.task.databinding.ActivityManageTaskBinding
import com.jop.task.util.ImageResource
import com.jop.task.util.SpinnerAdapter
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ManageTaskActivity : BaseActivity(), KodeinAware {
    override val kodein by kodein()
    private val binding by lazy { ActivityManageTaskBinding.inflate(layoutInflater) }
    private var todo: Todo? = null
    private var date: Calendar = Calendar.getInstance()
    private val format = SimpleDateFormat("dd MMMM HH:mm", Locale("id", "ID"))
    private var statusList = arrayListOf<Spinner>()
    private lateinit var adapterStatus: SpinnerAdapter
    private lateinit var defaulStatus: Spinner
    private var fileUri: Uri? = null
    private val manageTaskVMF: ManageTaskViewModelFactory by instance()
    private lateinit var manageTaskVM: ManageTaskViewModel

    private val startForImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                data?.data!!.let {
                    val cr = this.contentResolver
                    val fd = cr.openAssetFileDescriptor(it, "r")
                    val fs = fd?.length

                    if(fs!! <= 2097152){
                        fileUri = it
                        val bitmap = BitmapFactory.decodeFile(it.path)
                        ImageResource.clearGlide(binding.ivPicture)
                        ImageResource.setupBitmapToImageView(bitmap, binding.ivPicture)
                    } else {
                        showFailedToast("Gambar maksimal 2 MB")
                    }
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                showFailedToast(ImagePicker.getError(data))
            }
        }

    companion object {
        @JvmStatic
        fun newInstance(context: Context?, todo: Todo? = null): Intent {
            val bundle = Bundle().apply {
                putSerializable("todo", todo)
            }
            return Intent(context, ManageTaskActivity::class.java).putExtras(bundle)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Atur Task"

        manageTaskVM = ViewModelProvider(this, manageTaskVMF)[ManageTaskViewModel::class.java]

        statusList.add(Spinner("Pending", "pending"))
        statusList.add(Spinner("On Progress", "on_progress"))
        statusList.add(Spinner("Done", "done"))

        adapterStatus = SpinnerAdapter(this, statusList)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        binding.apply {
            if (intent.extras != null){
                todo = intent.extras!!.getSerializable("todo") as Todo?
            }

            if(todo != null){
                btnAdd.text = "Update"
                etTaskName.setText(todo!!.name)
                etSubjectName.setText(todo!!.subject)
                etDesc.setText(todo!!.desc)
                etDate.setText(todo?.createdAt?.let { format.format(it) })

                val indexOf = statusList.indexOfFirst { it.value == todo!!.status }
                defaulStatus = if(indexOf < 0) statusList[0] else statusList[indexOf]
                binding.etStatus.setText(defaulStatus.label, false)

                etRemainders.setText(todo?.remainder.toString())
                tlStatus.visibility = View.VISIBLE
                btnSelectPhoto.visibility = View.GONE

                date.time = todo!!.createdAt

                val bitmap = BitmapFactory.decodeByteArray(
                    todo!!.photo,0, todo!!.photo.size
                )
                ImageResource.setupBitmapToImageView(bitmap, binding.ivPicture)
            } else {
                etDate.setText(format.format(date.time))
                defaulStatus = statusList[0]
                binding.etStatus.setText(defaulStatus.label, false)
            }

            etStatus.setAdapter(adapterStatus)
            etStatus.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                defaulStatus = statusList[position]
                binding.etStatus.setText(defaulStatus.label, false)
            }

            etTaskName.doOnTextChanged { text, _, _, _ ->
                if (text.toString().length <= 2) binding.tlTaskName.error = "Minimal 3 karakter"
                else binding.tlTaskName.isErrorEnabled = false
            }

            etSubjectName.doOnTextChanged { text, _, _, _ ->
                if (text.toString().length <= 2) binding.tlSubjectName.error = "Minimal 3 karakter"
                else binding.tlSubjectName.isErrorEnabled = false
            }

            etDesc.doOnTextChanged { text, _, _, _ ->
                if (text.toString().length <= 2) binding.tlDesc.error = "Minimal 3 karakter"
                else binding.tlDesc.isErrorEnabled = false
            }

            etRemainders.doOnTextChanged { text, _, _, _ ->
                if (text.toString().isEmpty()) binding.tlRemainder.error = "Tidak boleh kosong"
                else binding.tlRemainder.isErrorEnabled = false
            }

            etDate.setOnClickListener { datePicker() }

            btnSelectPhoto.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    if (ContextCompat.checkSelfPermission(this@ManageTaskActivity, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                        toGallery()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(this@ManageTaskActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        toGallery()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }

            btnAdd.setOnClickListener {
                if(binding.etTaskName.text.toString().length <= 2){
                    binding.tlTaskName.error = "Minimal 3 karakter"
                } else if(binding.etSubjectName.text.toString().length <= 2){
                    binding.tlSubjectName.error = "Minimal 3 karakter"
                } else if(binding.etDesc.text.toString().length <= 2){
                    binding.tlDesc.error = "Minimal 3 karakter"
                } else if(binding.etRemainders.text.toString().isEmpty()){
                    binding.tlRemainder.error = "Tidak bolek kosong"
                } else if(fileUri == null && todo == null){
                    showFailedToast("File tidak boleh kosong")
                } else {
                    val setupTodo = Todo(
                        id = todo?.id ?: 0,
                        name = etTaskName.text.toString(),
                        subject = etSubjectName.text.toString(),
                        desc = etDesc.text.toString(),
                        status = defaulStatus.value,
                        createdAt = date.time,
                        remainder = etRemainders.text.toString().toInt(),
                        photo = if(todo == null) contentResolver.openInputStream(fileUri!!)?.readBytes() ?: byteArrayOf() else todo!!.photo
                    )

                    if(todo != null){
                        manageTaskVM.updateTodo(setupTodo)
                    } else {
                        manageTaskVM.insertTodo(setupTodo)
                    }

                    finish()
                }
            }
        }
    }

    private fun datePicker() {
        DatePickerDialog(this, { _, year, month, day ->
            TimePickerDialog(this, { _, hour, minute ->
                date.set(year, month, day, hour, minute, 0)
                binding.etDate.setText(format.format(date.time))
            }, date[Calendar.HOUR_OF_DAY], date[Calendar.MINUTE], true).show()
        }, date[Calendar.YEAR], date[Calendar.MONTH], date[Calendar.DAY_OF_MONTH]).show()
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
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) || (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
                showFailedToast("Izinkan aplikasi mengakses galeri")
            } else {
                openApplicationSetting()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(todo != null) menuInflater.inflate(R.menu.menu_delete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.delete){
            showCustomDialog(
                tittle = "Konfirmasi Hapus",
                content = "Apakah kamu yakin ingin menghapus task ini ?",
                btnConfirmText = "Ya, yakin",
                btnDismissText = "Tidak",
                btnDismissListener = {hideAlertDialog()},
                btnConfirmListener = {
                    hideAlertDialog()
                    manageTaskVM.deleteTodo(todo!!)
                    finish()
                }
            )
            return true
        }
        else{
            return super.onOptionsItemSelected(item)
        }
    }
}