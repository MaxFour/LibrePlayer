package com.maxfour.libreplayer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class ImageSaver(val context: Context) {
    private var external: Boolean = false
    private var directoryName: String = "LibrePlayer"
    private var fileName: String = "profile.png"

    fun setFileName(fileName: String): ImageSaver {
        this.fileName = fileName
        return this
    }

    fun setDirectoryName(directoryName: String): ImageSaver {
        this.directoryName = directoryName
        return this
    }

    fun setStoreType(external: Boolean): ImageSaver {
        this.external = external
        return this
    }

    fun save(bitmap: Bitmap) {
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(createFile())
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        } catch (er: Exception) {
            println(er)
        } finally {
            try {
                fileOutputStream?.close()
            } catch (er: IOException) {
                println(er)
            }
        }
    }

    fun getFile(): File {
        return createFile()
    }

    private fun createFile(): File {
        val directory: File = if (external) {
            getFileStorePlace(directoryName)
        } else {
            context.getDir(directoryName, Context.MODE_PRIVATE)
        }
        if (!directory.exists() && !directory.mkdirs()) {
            println("Error in creating folders $directory")
        }
        println("Create file -> $directory/$fileName")
        return File(directory, fileName)
    }

    private fun getFileStorePlace(directoryName: String): File {
        return File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), directoryName)
    }

    fun load(): Bitmap? {
        var inputStream: FileInputStream? = null
        return try {
            inputStream = FileInputStream(createFile())
            BitmapFactory.decodeStream(inputStream)
        } catch (er: Exception) {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            null
        }
    }
}
