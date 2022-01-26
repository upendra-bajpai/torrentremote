package com.grbworks.videoplayer.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video")
class Video(var videoUrl: String? = null,
            var watchedLength: Long? = null) {
    lateinit var title: String

    constructor(      title:String? =null,
                      videoUrl: String? = null,
                      watchedLength: Long? = null,
                      id: Int? = null
    ) : this(){
        this.id=id!!
        this.videoUrl=videoUrl
        this.watchedLength=watchedLength
        this.title=title!!
    }

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
