package com.maxfour.music.interfaces

import com.afollestad.materialcab.MaterialCab

interface CabHolder {

    fun openCab(menuRes: Int, callback: MaterialCab.Callback): MaterialCab
}
