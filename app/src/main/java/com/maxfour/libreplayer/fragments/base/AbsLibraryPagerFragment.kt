package com.maxfour.libreplayer.fragments.base

import android.os.Bundle
import com.maxfour.libreplayer.fragments.mainactivity.LibraryFragment

open class AbsLibraryPagerFragment : AbsMusicServiceFragment() {


    val libraryFragment: LibraryFragment
        get() = parentFragment as LibraryFragment

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }
}
