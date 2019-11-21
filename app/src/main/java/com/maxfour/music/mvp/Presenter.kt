package com.maxfour.music.mvp

import androidx.annotation.CallSuper

interface Presenter<T> {
    @CallSuper
    fun attachView(view: T)

    @CallSuper
    fun detachView()
}
