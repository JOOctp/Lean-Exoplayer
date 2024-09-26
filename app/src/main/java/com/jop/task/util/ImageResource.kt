package com.jop.task.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jop.task.R

class ImageResource {
    companion object {
        fun setupBitmapToImageView(bitmap: Bitmap, imageView: ImageView){
            Glide.with(imageView.context)
                .asBitmap()
                .load(bitmap)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(AppCompatResources.getDrawable(imageView.context, R.drawable.ic_placeholder))
                .into(imageView)
        }

        fun noCenterCropSizeOverride(bitmap: Bitmap, imageView: ImageView){
            Glide.with(imageView.context)
                .asBitmap()
                .load(bitmap)
                .override(0, 0)
                .error(AppCompatResources.getDrawable(imageView.context, R.drawable.ic_placeholder))
                .into(imageView)
        }

        fun clearGlide(imageView: ImageView){
            Glide.with(imageView.context).clear(imageView)
        }

        fun getRealPathFromURI(context: Context, contentUri: Uri): String {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(contentUri, projection, null, null, null)
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            val filePath = cursor?.getString(columnIndex!!)
            cursor?.close()
            return filePath!!
        }
    }
}