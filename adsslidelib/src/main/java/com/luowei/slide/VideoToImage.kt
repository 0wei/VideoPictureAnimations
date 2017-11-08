package com.luowei.slide

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream

/**
 * Created by luowei on 2017/11/7.
 */
object VideoToImage {
    fun saveImage(context: Context, path: String, dstPath :String) {
        val bitmap = Glide.with(context).asBitmap().load(path).submit().get()
        val outputStream = FileOutputStream(dstPath)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    }
}