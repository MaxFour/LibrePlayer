package com.maxfour.libreplayer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Song(
        val id: Int,
        val title: String,
        val songNumber: Int,
        val year: Int,
        val duration: Long,
        val data: String,
        val dateModified: Long,
        val albumId: Int,
        val albumName: String,
        val artistId: Int,
        val artistName: String,
        val composer: String?
) : Parcelable {


    companion object {
        @JvmStatic
        val emptySong = Song(
                -1,
                "",
                -1,
                -1,
                -1,
                "",
                -1,
                -1,
                "",
                -1,
                "",
                ""
        )
    }
}
