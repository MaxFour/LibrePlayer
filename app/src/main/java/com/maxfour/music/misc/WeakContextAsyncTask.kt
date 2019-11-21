package com.maxfour.music.misc

import android.content.Context
import android.os.AsyncTask

import java.lang.ref.WeakReference

abstract class WeakContextAsyncTask<Params, Progress, Result>(context: Context) : AsyncTask<Params, Progress, Result>() {
    private val contextWeakReference: WeakReference<Context> = WeakReference(context)

    protected val context: Context?
        get() = contextWeakReference.get()

}