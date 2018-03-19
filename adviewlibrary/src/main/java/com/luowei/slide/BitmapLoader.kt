package com.luowei.slide

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.LruCache
import java.lang.ref.SoftReference
import java.util.*


/**
 * Created by luowei on 2018/1/24.
 *
 */
object BitmapLoader {

    // Get max available VM memory, exceeding this amount will throw an
    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
    // int in its constructor.
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    // Use 1/8th of the available memory for this memory cache.
    private val cacheSize = maxMemory / 8
    private val mReusableBitmaps: MutableSet<SoftReference<Bitmap>> = Collections.synchronizedSet(HashSet())

    private var mMemoryCache: LruCache<String, Bitmap> = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return bitmap.byteCount / 1024
        }

        override fun entryRemoved(evicted: Boolean, key: String?, oldValue: Bitmap?, newValue: Bitmap?) {
            super.entryRemoved(evicted, key, oldValue, newValue)
            if (evicted && oldValue != null) {
//                Log.d("添加到复用")
                mReusableBitmaps.add(SoftReference(oldValue))
            }
        }
    }

    private fun getBitmapFromReusableSet(options: BitmapFactory.Options): Bitmap? {
        //BEGIN_INCLUDE(get_bitmap_from_reusable_set)
        var bitmap: Bitmap? = null

        if (!mReusableBitmaps.isEmpty()) {
            synchronized(mReusableBitmaps, {
                val iterator = mReusableBitmaps.iterator()
                var item: Bitmap?

                while (iterator.hasNext()) {
                    item = iterator.next().get()

                    if (null != item && item.isMutable) {
                        // Check to see it the item can be used for inBitmap
                        if (canUseForInBitmap(item, options)) {
                            bitmap = item

                            // Remove from reusable set so it can't be used again
                            iterator.remove()
                            break
                        }
                    } else {
                        // Remove from the set if the reference has been cleared.
                        iterator.remove()
                    }
                }
            })
        }

        return bitmap
        //END_INCLUDE(get_bitmap_from_reusable_set)
    }

    private fun hasKitKat(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun canUseForInBitmap(
            candidate: Bitmap, targetOptions: BitmapFactory.Options): Boolean {
        //BEGIN_INCLUDE(can_use_for_inbitmap)
        if (!hasKitKat()) {
            // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
            return (candidate.width == targetOptions.outWidth
                    && candidate.height == targetOptions.outHeight
                    && targetOptions.inSampleSize == 1)
        }

        // From Android 4.4 (KitKat) onward we can re-use if the byte size of the new bitmap
        // is smaller than the reusable bitmap candidate allocation byte count.
        val width = targetOptions.outWidth / targetOptions.inSampleSize
        val height = targetOptions.outHeight / targetOptions.inSampleSize
        val byteCount = width * height * getBytesPerPixel(candidate.config)
        return byteCount <= candidate.allocationByteCount
        //END_INCLUDE(can_use_for_inbitmap)
    }

    private fun getBytesPerPixel(config: Bitmap.Config): Int {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4
        } else if (config == Bitmap.Config.RGB_565) {
            return 2
        } else if (config == Bitmap.Config.ARGB_4444) {
            return 2
        } else if (config == Bitmap.Config.ALPHA_8) {
            return 1
        }
        return 1
    }

    private fun addBitmapToMemoryCache(key: String, bitmap: Bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap)
        }
    }

    private fun getBitmapFromMemCache(key: String): Bitmap? {
        return mMemoryCache.get(key)
    }

    fun loadBitmap(file: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        val imageKey = file
        var bitmap = getBitmapFromMemCache(imageKey)
        if (bitmap != null) {
            return bitmap
        } else {
            bitmap = decodeSampledBitmapFromFile(file, reqWidth, reqHeight)
            addBitmapToMemoryCache(imageKey, bitmap)
            return bitmap
        }
    }

    private fun calculateInSampleSize(
            options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun addInBitmapOptions(options: BitmapFactory.Options) {
        // inBitmap only works with mutable bitmaps, so force the decoder to
        // return mutable bitmaps.
        options.inMutable = true

        // Try to find a bitmap to use for inBitmap.
        val inBitmap = getBitmapFromReusableSet(options)

        if (inBitmap != null) {
            // If a suitable bitmap has been found, set it as the value of
            // inBitmap.
//            Log.d("复用..")
            options.inBitmap = inBitmap
        }
    }

    private fun decodeSampledBitmapFromFile(file: String, reqWidth: Int, reqHeight: Int): Bitmap {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file, options)
        if (reqWidth > 0 && reqHeight > 0) {
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        }
        addInBitmapOptions(options)
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        options.outWidth
        return BitmapFactory.decodeFile(file, options)
    }
}