package com.grbworks.videoplayer.presentation

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grbworks.videoplayer.R
import com.grbworks.videoplayer.base.BaseRecyclerAdapter
import com.grbworks.videoplayer.data.database.Video

import kotlinx.android.synthetic.main.activity_home.*

import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import com.grbworks.videoplayer.data.model.VideoSource
import com.grbworks.videoplayer.data.model.VideoSource.SingleVideo
import com.grbworks.videoplayer.presentation.player.PlayerActivity

import android.text.InputType

import android.widget.EditText
import android.view.*
import java.io.File

import android.os.Environment
import androidx.appcompat.app.AlertDialog
import com.github.se_bastiaan.torrentstream.StreamStatus
import com.github.se_bastiaan.torrentstream.Torrent
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener
import com.grbworks.videoplayer.data.model.dataModal.SeriesModals
import com.google.gson.Gson
import kotlinx.android.synthetic.main.content_show_data.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import java.io.IOException

import java.io.FileReader

import java.io.BufferedReader
import java.lang.Exception


class Home : AppCompatActivity() {
    private val KEY_DATA: String="json_data"
    private var data1:String?=""
    lateinit var shows:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        shows=findViewById<RecyclerView>(R.id.episode)
        fab.setOnClickListener { view ->
            listDirs()
            Snackbar.make(view, "Refresh", Snackbar.LENGTH_LONG)
                    .setAction("Ok", null).show()
        }

        if(intent.getStringExtra(KEY_DATA)==null)
            listDirs()
        else {
            data1 = intent.getStringExtra(KEY_DATA) as String
            Log.d("alex","intent.getStringExtra() intent")
            //get(KEY_DATA) as String
            doAsync {
                //val json = getJsonfromFile(data1)
                var gson = Gson().fromJson(data1, SeriesModals::class.java)
                runOnUiThread {
                    val list = ArrayList<Video>()
                    for (i in gson.details.indices)
                        for (j in gson.details[i].episodes.indices)
                            list.add(Video(gson.details[i].episodes[j].name,gson.details[i].episodes[j].url, 0, i))
                    showInfo(list)
                }
            }
        }

    }
    
    private fun showInfo( data:ArrayList<Video>) {
        val layoutManager=LinearLayoutManager(baseContext)
        shows.layoutManager=layoutManager
        val dataAdapter=object:BaseRecyclerAdapter<Video>(applicationContext,data){
            override fun setViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
                val inflater = LayoutInflater.from(parent?.context).inflate(R.layout.content_show_data, parent, false)
                return ItemViewHolder(parent!!.context, inflater)
            }

            override fun onBindData(holder: RecyclerView.ViewHolder?, value: Video?) {
              val holder1=holder as ItemViewHolder
                holder1.name.text=value!!.title
                holder1.link.text=value.videoUrl

            }
        }
        shows.adapter=dataAdapter

    }

    fun alertBox(title:String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)

        val input = EditText(this)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK") { dialog, which ->
            run {
                val m_Text = input.text.toString()
                val urlList = ArrayList<Video>()
                urlList.add(Video(m_Text.trim(), 0))
                when {
                    title == "Movie URl" -> {
                        goToPlayerActivity(makeVideoSource(urlList, 0))
                    }
                    else -> {
                        val intent =Intent(this,TorrentActivity.javaClass)
                        //intent.putExtra(TorrentActivity.KEY_URL_MAGENT,m_Text.trim())
                        intent.putExtra(TorrentActivity.KEY_URL_MAGENT,"https://yts.mx/torrent/download/04E2C2204BE8F55CCCA7B5BE681E0A63392E885C")
                        startActivity(intent)
                    }
                }
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private inner class ItemViewHolder(context: Context?, itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var name: TextView = itemView.findViewById(R.id.title)
        var link: TextView = itemView.findViewById(R.id.link)
        init {
            name.setOnClickListener(this)
            link.setOnClickListener(this)
        }
        var context:Context = context!!
        override fun onClick(v: View) {
            Log.d("alex", "onClick: ")
            when (v.id) {
                R.id.title -> {
                   val url=link.text.toString()
                    Log.d("alex",url)
                    val urlList=ArrayList<Video>()
                    urlList.add(Video(url,0))
                    if (url.contains("http")){
                        goToPlayerActivity(makeVideoSource(urlList, 0))
                    }else if(url.contains("json")){
                        doAsync {
                            val json=getJsonfromFile(url)
                            Log.d("alex",json)
                            runOnUiThread{
                                val list= ArrayList<Video>()
                                sendIntent(json)
                                //showInfo(list)
                            }
                        }
                    }else{
                        Log.d("alex","intent.getStringExtra()")
                        if (intent!=null) {

                        }
                    }
                }
            }
        }

        private fun sendIntent(data:String){
            val intent = Intent(this@Home,Home::class.java)
            intent.putExtra(KEY_DATA,data)
            startActivity(intent)
        }

        private fun getJsonfromFile(url: String):String {
            //Get the text file
            val file = File(url)
            //Read text from file
            val text = StringBuilder()

            try {
                val br = BufferedReader(FileReader(file))
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    text.append(line)
                }
                br.close()
            } catch (e: IOException) {
                //You'll need to add proper error handling here
            }
            return text.toString()
        }

        init {
            link.setOnClickListener(this)
            name.setOnClickListener { v: View -> onClick(v) }
        }


    }

    private fun makeVideoSource(videos: List<Video>, index: Int): VideoSource? {
        val singleVideos: ArrayList<SingleVideo> = ArrayList()
        for (i in videos.indices) {
            singleVideos.add(i, SingleVideo(
                    videos[i].videoUrl,
                    null,
                    0)
            )
        }
        return VideoSource(singleVideos, index)
    }

    fun goToPlayerActivity(videoSource: VideoSource?) {
         val REQUEST_CODE = 1000
        val intent = Intent(baseContext, PlayerActivity::class.java)
        intent.putExtra("videoSource", videoSource)
        startActivityForResult(this@Home,intent,REQUEST_CODE,null)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        // return true so that the menu pop up is opened
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item.getItemId()){
           R.id.search->{
               Log.d("alex","item")
               alertBox("Movie URl")
           }
           R.id.torrent->{
               alertBox("Torrent Link")
           }
       }

        return true
    }

    fun listDirs(){
         val videoFiles:ArrayList<Video> =ArrayList()
        doAsync {
            val files=scanDir()
            for (i in files!!.indices) {
                Log.d("Files", "FileName:" + files[i].name)
                videoFiles.add(Video((files[i].name),(files[i].absolutePath),0,i))
            }
            runOnUiThread{
                showInfo(videoFiles)
            }
        }

    }
    
    fun scanDir(): Array<out File>? {
        val path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Environment.getExternalStoragePublicDirectory("xxx").absolutePath
        } else {
            TODO("VERSION.SDK_INT < KITKAT")
        }
        val tt=if(File(path).exists())  0 else 1
        Log.d("Files", "Path: $path n $tt")
        val directory = File(path)
        val files= directory.listFiles()
        Log.d("Files", "Size: " + files.size)
        for (i in files.indices) {
            Log.d("Files", "FileName:" + files[i].name)
        }
        return files
    }

}
