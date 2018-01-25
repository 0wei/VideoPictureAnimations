package com.luowei.slide

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.unistrong.luowei.adsslidelib.R
import com.unistrong.luowei.commlib.Log
import com.unistrong.luowei.kotlin.hide
import com.unistrong.luowei.kotlin.show
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by luowei on 2017/12/5.
 */
class StaticAdvertisement : AbsAdvertisement {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private val DEBUG = true
    var timeOut = 3000
        set(value) {
            field = Math.max(50, value)
        }
    var currentIndex = 0
        private set(value) {
            field = value
//            if (DEBUG) Log.d("currentIndex=$currentIndex")
            indicator.onPageSelected(currentIndex)
        }
    val playlist = ArrayList<SlideAdapter.Item>()

    private var timerHandler: Handler? = MyHandler(this)

    val roll3dContainer = Roll3DContainer(context)
    private val textureView = VideoView(context)
    private val indicator = CircleIndicator(context)

    init {
        addView(textureView)
        addView(roll3dContainer)
        val layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        layoutParams.bottomMargin = 10
        indicator.setPadding(0, 10, 0, 10)
        indicator.gravity = Gravity.CENTER_HORIZONTAL
//        indicator.setBackgroundColor(context.resources.getColor(R.color.indicator_background))
        addView(indicator, layoutParams)
        val listener: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                updateItem()
            }
        }
        roll3dContainer.listener = listener
        textureView.listener = {
            if (it == VideoView.ListenState.End) {
                if (DEBUG) Log.d("play ok")
                roll3dContainer.currentBitmap = textureView.bitmap
                roll3dContainer.show()
                textureView.hide()
                slideNext(true)
            } else{
                val alpha = roll3dContainer.animate().alpha(0f)
                alpha.duration = 1000
                alpha.setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        roll3dContainer.hide()
                        roll3dContainer.alpha = 1f
                    }
                })
                alpha.start()
            }
        }
    }

    private fun updateItem() {
        if (DEBUG) Log.d()
        if (currentItem!!.type == SlideAdapter.ItemType.Image) {
            slideDelay()
        } else {
            textureView.play()
//            val alpha = roll3dContainer.animate().alpha(0f)
//            alpha.duration = 1000
//            alpha.setListener(object : AnimatorListenerAdapter() {
//                override fun onAnimationEnd(animation: Animator?) {
//                    super.onAnimationEnd(animation)
//                    roll3dContainer.hide()
//                    roll3dContainer.alpha = 1f
//                }
//            })
//            alpha.start()
        }
    }

    override fun clear() {
        playlist.clear()
        notifyDataChange()
    }

    override fun addItem(item: SlideAdapter.Item) {
//        super.addItem(item)
        playlist.add(item)
        notifyDataChange()
    }

    private fun slideDelay() {
        if (DEBUG) Log.d()
        timerHandler!!.removeMessages(0)
        timerHandler!!.sendEmptyMessageDelayed(0, timeOut.toLong())
    }

    private var currentItem: SlideAdapter.Item? = null

    private fun slideNext(force: Boolean = false) {
        if (playlist.size <= 1) {
            if (currentItem?.type == SlideAdapter.ItemType.Video) {
                updateItem()
            }
            return
        }
        if (currentItem?.type == SlideAdapter.ItemType.Video && !force) {
//            slideDelay()
            if (DEBUG) Log.d("wait..")
            return
        }

        if (currentIndex + 1 >= playlist.size) currentIndex = 0 else currentIndex++
        currentItem = playlist[currentIndex]
//        roll3dContainer.nextBitmap = getImagePath(currentItem!!)
        if (currentItem!!.type == SlideAdapter.ItemType.Video) {
            textureView.initVideoResource(currentItem!!.path)
            updateItem()
        } else {
            val bitmap = getImagePath(currentItem!!)
            if (bitmap == null) {
                Log.d("${currentItem!!.path} bitmap is empty slide next(${playlist.size})")
                playlist.removeAt(currentIndex)
//                slideNext(true)
                notifyDataChange()
            } else {
                roll3dContainer.nextBitmap = bitmap
            }
        }
    }

    private fun getImagePath(item: SlideAdapter.Item): Bitmap? {
        val path = when (item.type) {
            SlideAdapter.ItemType.Image -> item.path
            else -> item.videoImage
        }
        return try {
            Log.d("path=$path")
//            val options = BitmapFactory.Options()
//            options.inMutable = true
//            options.inBitmap = bitmap
//            BitmapFactory.decodeFile(path, options)
//            Glide.with(this).asBitmap().load(path).submit().get()
//            Glide.with(this)
//            BitmapFactory.decodeFile(path)
            path ?: return null
            return BitmapLoader.loadBitmap(path, width, height)
        } catch (e: Exception) {
            Log.e(e.localizedMessage)
            null
        }

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        slideDelay()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        timerHandler!!.removeMessages(0)
    }

    internal class MyHandler(pager: StaticAdvertisement) : Handler() {
        private var pager: WeakReference<StaticAdvertisement> = WeakReference(pager)

        override fun handleMessage(msg: Message) {
            pager.get()?.slideNext()
        }
    }

    private lateinit var defaultPath: String

    override fun setDefaultImageFile(path: String) {
        defaultPath = path
        notifyDataChange()
    }

    fun notifyDataChange() {
        if (playlist.size == 1) {
            val item = playlist[0]
            currentItem = item
            roll3dContainer.currentBitmap = getImagePath(item)
            if (currentItem!!.type == SlideAdapter.ItemType.Video) {
                textureView.initVideoResource(currentItem!!.path)
                updateItem()
            }
        } else if (playlist.size == 0) {
            roll3dContainer.currentBitmap = BitmapFactory.decodeFile(defaultPath)
        }
        indicator.updateIndicator(playlist.size)
        slideDelay()
    }
}