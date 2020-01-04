package com.maxfour.libreplayer.interfaces

import com.afollestad.materialcab.MaterialCab

interface CabHolder {

    fun openCab(menuRes: Int, callback: MaterialCab.Callback): MaterialCab
}
