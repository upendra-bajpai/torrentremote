package com.grbworks.videoplayer.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.grbworks.videoplayer.R
import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.github.se_bastiaan.torrentstream.StreamStatus
import com.github.se_bastiaan.torrentstream.Torrent
import com.github.se_bastiaan.torrentstream.TorrentOptions
import com.github.se_bastiaan.torrentstream.TorrentStream
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener
import com.grbworks.videoplayer.BuildConfig
import com.grbworks.videoplayer.data.database.Video
import com.grbworks.videoplayer.data.model.VideoSource
import com.grbworks.videoplayer.presentation.TorrentActivity.Companion.TORRENT
import com.grbworks.videoplayer.presentation.player.PlayerActivity
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

@SuppressLint("SetTextI18n")
class TorrentActivity : AppCompatActivity(), TorrentListener {

    private lateinit var button: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var torrentStream: TorrentStream
    private lateinit var streamUrl:String
    var onClickListener = View.OnClickListener {
        progressBar.progress = 0
        if (torrentStream.isStreaming) {
            torrentStream.stopStream()
            button.text = "Start stream"
            return@OnClickListener
        }
        torrentStream.startStream(streamUrl)
        button.text = "Stop stream"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_torrent)
        if (intent!=null) {
            try {
                streamUrl = URLDecoder.decode(intent.getStringExtra(KEY_URL_MAGENT).toString(), "utf-8")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }
        val torrentOptions = TorrentOptions.Builder()
                .saveLocation(filesDir)
                .removeFilesAfterStop(true)
                .build()
        torrentStream = TorrentStream.init(torrentOptions)
        torrentStream.addListener(this)
        button = findViewById(R.id.button)
        button.setOnClickListener(onClickListener)
        progressBar = findViewById(R.id.progress)
        progressBar.max = 100
    }

    override fun onStreamPrepared(torrent: Torrent) {
        Log.d(TORRENT, "onStreamPrepared")
        // If you set TorrentOptions#autoDownload(false) then this is probably the place to call
        // torrent.startDownload();
    }

    override fun onStreamStarted(torrent: Torrent) {
        Log.d(TORRENT, "onStreamStarted")
    }

    override fun onStreamError(torrent: Torrent, e: Exception) {
        Log.e(TORRENT, "onStreamError", e)
        button.text = "Start stream"
    }
    fun goToPlayerActivity(videoSource: VideoSource?) {
        val REQUEST_CODE = 1000
        val intent = Intent(baseContext, PlayerActivity::class.java)
        intent.putExtra("videoSource", videoSource)
        ActivityCompat.startActivityForResult(this@TorrentActivity, intent, REQUEST_CODE, null)
        finish()
    }
    private fun makeVideoSource(videos: List<Video>, index: Int): VideoSource? {
        val singleVideos: ArrayList<VideoSource.SingleVideo> = ArrayList()
        for (i in videos.indices) {
            singleVideos.add(i, VideoSource.SingleVideo(
                    videos[i].videoUrl,
                    null,
                    0)
            )
        }
        return VideoSource(singleVideos, index)
    }
    override fun onStreamReady(torrent: Torrent) {
        progressBar.progress = 100
        val mediaFile = torrent.videoFile
        Log.d(TORRENT, "onStreamReady: ${mediaFile.path}")
        val urlList = ArrayList<Video>()
        urlList.add(Video(mediaFile.path, 0))
        goToPlayerActivity(makeVideoSource(urlList, 0))
        // Create a sharing intent
     /*   startActivity(Intent().apply {
            action = Intent.ACTION_VIEW
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mediaFile.extension)
            val authority = "${BuildConfig.APPLICATION_ID}.provider"
            data = FileProvider.getUriForFile(this@TorrentActivity, authority, mediaFile)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
        })*/
    }

    override fun onStreamProgress(torrent: Torrent, status: StreamStatus) {
        if (status.bufferProgress <= 100 && progressBar.progress < 100 && progressBar.progress != status.bufferProgress) {
            Log.d(TORRENT, "Progress: " + status.bufferProgress)
            progressBar.progress = status.bufferProgress
        }
    }

    override fun onStreamStopped() {
        Log.d(TORRENT, "onStreamStopped")
    }

    companion object {
        private const val TORRENT = "Torrent"
        public const  val KEY_URL_MAGENT="htyr"

    }
}