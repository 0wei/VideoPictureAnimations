package com.luowei.slide

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Message
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import com.unistrong.luowei.commlib.Log
import com.unistrong.luowei.kotlin.hide
import com.unistrong.luowei.kotlin.show
import java.lang.ref.WeakReference
import java.util.ArrayList

/**
 * Created by luowei on 2017/12/5.
 */
class StaticAdvertisement : AbsAdvertisement {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private val DEBUG = false
    private var timeOut = 2000
    var currentIndex = 0
        private set
    private val playlist = ArrayList<SlideAdapter.Item>()
    private var timerHandler: Handler? = MyHandler(this)

    private val roll3dContainer = Roll3DContainer(context)
    private val textureView = VideoView(context)

    init {
        addView(textureView)
        addView(roll3dContainer)
        val listener: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                updateItem()
            }
        }
        roll3dContainer.listener = listener
        textureView.listener = {
            if (DEBUG) Log.d("play ok")
            roll3dContainer.currentBitmap = textureView.bitmap
            roll3dContainer.show()
            textureView.hide()
            slideNext(true)
        }
    }

    private fun updateItem() {
        if (DEBUG) Log.d()
        if (currentItem!!.type == SlideAdapter.ItemType.Image) {
            slideDelay()
        } else {
            textureView.play()
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


    override fun addItem(item: SlideAdapter.Item) {
        super.addItem(item)
        if (playlist.size == 0) {
            currentItem = item
            roll3dContainer.currentBitmap = getImagePath(item)
            if (currentItem!!.type == SlideAdapter.ItemType.Video) {
                textureView.initVideoResource(currentItem!!.path)
                updateItem()
            }
            currentIndex++
        }
        playlist.add(item)
        slideDelay()
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
            slideDelay()
            if (DEBUG) Log.d("wait..")
            return
        }

        if (currentIndex + 1 > playlist.size) currentIndex = 0
        currentItem = playlist[currentIndex++]
//        roll3dContainer.nextBitmap = getImagePath(currentItem!!)
        if (currentItem!!.type == SlideAdapter.ItemType.Video) {
            textureView.initVideoResource(currentItem!!.path)
            updateItem()
        } else {
            roll3dContainer.nextBitmap = getImagePath(currentItem!!)
        }
    }

    private fun getImagePath(item: SlideAdapter.Item): Bitmap? {
        val path = when (item.type) {
            SlideAdapter.ItemType.Image -> item.path
            else -> item.videoImage

        }
        return BitmapFactory.decodeFile(path)

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
        var pager: WeakReference<StaticAdvertisement> = WeakReference(pager)

        override fun handleMessage(msg: Message) {
            pager.get()?.slideNext()

        }
    }

    override fun setDefaultImageFile(path: String) {
        roll3dContainer.currentBitmap = BitmapFactory.decodeFile(path)
    }
}