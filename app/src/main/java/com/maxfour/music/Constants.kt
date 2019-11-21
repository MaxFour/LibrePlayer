package com.maxfour.music

import android.provider.BaseColumns
import android.provider.MediaStore

object Constants {

    const val GITHUB_PROJECT = "https://github.com/MaxFour/Music-Player-2"
    const val USER_PROFILE = "profile.jpg"
    const val USER_BANNER = "banner.jpg"

    const val BASE_SELECTION = MediaStore.Audio.AudioColumns.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''"

    val baseProjection = arrayOf(BaseColumns._ID, // 0
            MediaStore.Audio.AudioColumns.TITLE, // 1
            MediaStore.Audio.AudioColumns.TRACK, // 2
            MediaStore.Audio.AudioColumns.YEAR, // 3
            MediaStore.Audio.AudioColumns.DURATION, // 4
            MediaStore.Audio.AudioColumns.DATA, // 5
            MediaStore.Audio.AudioColumns.DATE_MODIFIED, // 6
            MediaStore.Audio.AudioColumns.ALBUM_ID, // 7
            MediaStore.Audio.AudioColumns.ALBUM, // 8
            MediaStore.Audio.AudioColumns.ARTIST_ID, // 9
            MediaStore.Audio.AudioColumns.ARTIST,// 10
            MediaStore.Audio.AudioColumns.COMPOSER)// 11
    const val NUMBER_OF_TOP_TRACKS = 99

}
