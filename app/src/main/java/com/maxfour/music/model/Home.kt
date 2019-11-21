package com.maxfour.music.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.maxfour.music.adapter.HomeAdapter.Companion.HomeSection

class Home(val priority: Int,
           @StringRes val title: Int,
           @StringRes val subTitle: Int,
           val arrayList: ArrayList<*>,
           @HomeSection
           val homeSection: Int,
           @DrawableRes
           val icon: Int)