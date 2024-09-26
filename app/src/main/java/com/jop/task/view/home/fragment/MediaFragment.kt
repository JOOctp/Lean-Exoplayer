package com.jop.task.view.home.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jop.task.base.ui.BaseFragment
import com.jop.task.base.ui.BaseRcAdapter
import com.jop.task.databinding.FragmentMediaBinding
import com.jop.task.util.SizeUtil
import com.jop.task.util.SpacingItemColumnAdapter
import com.jop.task.view.adapter.CalendarAdapter
import com.jop.task.view.adapter.MediaAdapter
import com.jop.task.view.mediaPlayer.MediaPlayerActivity

class MediaFragment : BaseFragment(), BaseRcAdapter.OnItemClickListener<Uri> {
    private lateinit var binding: FragmentMediaBinding
    private lateinit var adapter: MediaAdapter

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK){
            val data = it.data
            val listOfUri = mutableListOf<Uri>()

            if(data != null) {
                if(data.clipData != null){
                    for (i in 0 until data.clipData!!.itemCount) {
                        val uri: Uri = data.clipData!!.getItemAt(i).uri
                        listOfUri.add(uri)
                    }
                } else if(data.data != null){
                    listOfUri.add(data.data!!)
                }

                if(adapter.getDataOriginal().isEmpty()) adapter.setData(listOfUri)
                else adapter.addAllData(listOfUri)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMediaBinding.inflate(inflater, container, false)
        adapter = MediaAdapter()
        adapter.setOnClickListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val decoration = SpacingItemColumnAdapter(3, SizeUtil.pxFromDp(16f), false)
        val gridLayout  = object : GridLayoutManager(requireContext(), 3){
            override fun canScrollHorizontally(): Boolean {
                return false
            }
            override fun canScrollVertically(): Boolean {
                return true
            }
        }

        binding.apply {
            rcMedia.addItemDecoration(decoration)
            rcMedia.layoutManager = gridLayout
            rcMedia.adapter = adapter

            fabAddMedia.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                        pickMedia()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        pickMedia()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            pickMedia()
        } else {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)) || (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU && ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE))) {
                showFailedToast("Izinkan aplikasi mengakses galeri")
            } else {
                openApplicationSetting()
            }
        }
    }

    private fun pickMedia(){
        val mimetypes = arrayOf("image/*", "video/*")
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.setType("*/*")
        chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
        chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        getResult.launch(chooseFile)
    }

    override fun onItemClick(v: View?, position: Int, item: Uri) {
        val listMedia : ArrayList<Uri> = arrayListOf()
        listMedia.addAll(adapter.getDataOriginal())
        listMedia.removeAt(position)
        listMedia.add(0, item)

        startActivity(MediaPlayerActivity.newInstance(context, listMedia))
    }
}