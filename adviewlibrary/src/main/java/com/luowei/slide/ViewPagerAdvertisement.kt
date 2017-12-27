package com.luowei.slide

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.FragmentManager
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import com.unistrong.luowei.adsslidelib.R
import java.io.File
import java.io.FileOutputStream

/**
 * Created by luowei on 2017/9/11.
 */
class ViewPagerAdvertisement : FrameLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    var viewPager: SlideViewPager = SlideViewPager(context)
    private val indicator = CircleIndicator(context)
    lateinit var adapter: SlideAdapter
    val DEFAULT_IMAGE: String
    var workHandler: Handler
    private val BASEPATH = context.getExternalFilesDir("Advertisement").absolutePath

    init {
        viewPager.id = R.id.advertisementViewPager
        addView(viewPager)
        val layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        layoutParams.bottomMargin = 10
        indicator.setPadding(0, 10, 0, 10)
        indicator.gravity = Gravity.CENTER_HORIZONTAL
//        indicator.setBackgroundColor(context.resources.getColor(R.color.indicator_background))
//        addView(indicator, layoutParams)


        viewPager

        val file = File(BASEPATH, "ADS_DEFAULT.jpg")
        DEFAULT_IMAGE = file.absolutePath
        if (!file.exists()) {
            context.resources.openRawResource(R.raw.ads).copyTo(FileOutputStream(file))
        }
        val handlerThread = HandlerThread("AdvertisementVideoToImageThread")
        handlerThread.start()
        workHandler = Handler(handlerThread.looper)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        workHandler.looper.quit()
    }


    fun addItem(item: SlideAdapter.Item) {
        if (item.type == SlideAdapter.ItemType.Video) {
            item.videoImage = File(BASEPATH, File(item.path).name).absolutePath
            workHandler.post { VideoToImage.saveImage(context, item.path, item.videoImage!!) }
        }
        adapter.addItem(item)
    }

    fun clear() {
        adapter.clear()
    }

    fun getBitmap(): Bitmap? {
        return if (adapter.currentFragment is VideoFragment) {
            (adapter.currentFragment as VideoFragment).getBitmap()
        } else {
            (adapter.currentFragment as ImageShowFragment).getBitmap()
        }
    }

    fun setFragmentManager(fm: FragmentManager) {
        adapter = SlideAdapter(fm, viewPager)
        viewPager.adapter = adapter
        adapter.setDefault(SlideAdapter.Item(SlideAdapter.ItemType.Image, DEFAULT_IMAGE))
    }

    fun setDefaultImageFile(path: String) {
        adapter.setDefault(SlideAdapter.Item(SlideAdapter.ItemType.Image, path))
    }
}