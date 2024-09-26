package com.jop.task.view.mediaPlayer

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.jop.task.R
import com.jop.task.base.ui.BaseActivity
import com.jop.task.data.model.Todo
import com.jop.task.databinding.ActivityManageTaskBinding
import com.jop.task.databinding.ActivityMediaPlayerBinding
import com.jop.task.view.task.ManageTaskActivity

class MediaPlayerActivity : BaseActivity() {
    private val binding by lazy { ActivityMediaPlayerBinding.inflate(layoutInflater) }
    private val listMedia: ArrayList<Uri> = arrayListOf()
    private lateinit var player: ExoPlayer

    companion object {
        @JvmStatic
        fun newInstance(context: Context?, listMedia: ArrayList<Uri>): Intent {
            val bundle = Bundle().apply {
                putParcelableArrayList("media", listMedia)
            }
            return Intent(context, MediaPlayerActivity::class.java).putExtras(bundle)
        }
    }

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        if (intent.extras != null){
            listMedia.addAll(intent.extras!!.getParcelableArrayList<Uri>("media") as ArrayList<Uri>)
        }

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Media Player"

        player = ExoPlayer
            .Builder(this)
            .setSeekForwardIncrementMs(5000)
            .setSeekBackIncrementMs(5000)
            .build()

        listMedia.forEach {
            val mediaItem: MediaItem?
            val cR: ContentResolver = contentResolver
            val type = cR.getType(it)

            if(type!!.contains("image")){
                mediaItem = MediaItem.Builder().setUri(it).setImageDurationMs(1000).build()
            } else {
                mediaItem = MediaItem.fromUri(it)
            }

            player.addMediaItem(mediaItem)
        }

        binding.apply {
            playerView.player = player

            player.prepare()
            player.play()
        }
    }

    override fun onStart() {
        super.onStart()
        player.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        player.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        player.playWhenReady = false
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}