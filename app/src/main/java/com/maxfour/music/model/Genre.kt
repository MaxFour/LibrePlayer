package com.maxfour.music.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Genre(val id: Int = -1, val name: String, val songCount: Int) : Parcelable