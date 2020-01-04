package com.maxfour.libreplayer.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.maxfour.libreplayer.adapter.HomeAdapter.Companion.HomeSection

class Home(val priority: Int,
           @StringRes val title: Int,
           val arrayList: ArrayList<*>,
           @HomeSection
           val homeSection: Int,
           @DrawableRes
           val icon: Int)
