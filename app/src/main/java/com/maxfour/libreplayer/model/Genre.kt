package com.maxfour.libreplayer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Genre(val id: Int = -1, val name: String, val songCount: Int) : Parcelable
