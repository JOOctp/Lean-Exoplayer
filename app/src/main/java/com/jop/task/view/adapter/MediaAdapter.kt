package com.jop.task.view.adapter

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jop.task.base.ui.BaseRcAdapter
import com.jop.task.databinding.ItemLayoutMediaBinding
import com.jop.task.util.ImageResource

class MediaAdapter: BaseRcAdapter<Uri, MediaAdapter.ViewHolder>() {
    private var medias: MutableList<Uri> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemLayoutMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    class ViewHolder(private val binding: ItemLayoutMediaBinding) : BaseRcAdapter.ViewHolder<Any>(binding.root) {
        fun bind(item: Uri) {
            binding.apply {
                val cR: ContentResolver = root.context.contentResolver
                val type = cR.getType(item)

                ImageResource.clearGlide(ivItem)

                if(type!!.contains("image")){
                    ivPlay.visibility = View.GONE
                    val bitmap = BitmapFactory.decodeFile(ImageResource.getRealPathFromURI(root.context, item))
                    ImageResource.setupBitmapToImageView(bitmap, ivItem)
                } else {
                    val mmr = MediaMetadataRetriever()
                    mmr.setDataSource(ImageResource.getRealPathFromURI(root.context, item))
                    val thummbnailBitmap = mmr.frameAtTime

                    ImageResource.noCenterCropSizeOverride(thummbnailBitmap!!, ivItem)
                    ivPlay.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            val media: Uri? = getItem(position)
            holder.bind(media!!)
            holder.item = media
        }
        super.onBindViewHolder(holder, position)
    }

    override fun setData(list: MutableList<Uri>?) {
        this.medias = list!!
        super.setData(list)
    }

    fun getDataOriginal(): MutableList<Uri> {
        return this.medias
    }

    fun addAllData(medias: List<Uri>) {
        this.medias.addAll(medias)
        notifyDataSetChanged()
    }
}