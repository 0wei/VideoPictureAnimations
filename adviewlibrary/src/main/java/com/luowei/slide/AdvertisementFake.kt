package com.luowei.slide

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import com.unistrong.luowei.commlib.Log
import java.util.*

/**
 * Created by luowei on 2017/12/4.
 *
 */
class AdvertisementFake : AbsAdvertisement {


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    val playlist = ArrayList<SlideAdapter.Item>()
    private var currentIndex = 0
        set(value) {
            Log.d("current=$value")
            field = value
        }
    val ImageViewA = Roll3DContainer(context)
    val ImageViewB = Roll3DContainer(context)
    val ImageViewC = Roll3DContainer(context)
    val recycleView = LinkedList<Roll3DContainer>()
    var viewWidth = 0

    init {
        ImageViewA.setBackgroundColor(android.graphics.Color.RED)
        ImageViewB.setBackgroundColor(android.graphics.Color.YELLOW)
        ImageViewC.setBackgroundColor(android.graphics.Color.BLUE)
        recycleView.offer(ImageViewA)
        recycleView.offer(ImageViewB)
        recycleView.offer(ImageViewC)
        addView(ImageViewB)
        addView(ImageViewA)
        addView(ImageViewC)
    }

    var velocity = 0f
    val detector = GestureDetector(context, object : GestureDetector.OnGestureListener {
        override fun onShowPress(e: MotionEvent?) {
            Log.d()
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            Log.d()
            return false
        }

        override fun onDown(e: MotionEvent?): Boolean {
            Log.d()
//            recycleView.get(1).performClick()
            return false
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            Log.d("velocityX=$velocityX, velocityY=$velocityY")
            velocity = velocityX
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
//            Log.d("distanceX=$distanceX")
            recycleView(-distanceX)
//            Log.d("left = ${ImageViewA.left},right=${ImageViewA.right} translationX=${ImageViewA.translationX}, scrollX=${ImageViewA.scrollX}," +
//                    "x=${ImageViewA.x}")
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            Log.d()
        }
    })

    fun recycleView(distance: Float) {
        recycleView.forEachIndexed { index, roll3DContainer ->
            roll3DContainer.x += distance
        }
        val left = -viewWidth * 3 / 2
        if (recycleView.first.x < left) {
//            currentIndex++
            val removeFirst = recycleView.removeFirst()
            removeFirst.x = recycleView.last.x + recycleView.last.width
            recycleView.offer(removeFirst)
        }
        val right = viewWidth * 3 / 2
        if (recycleView.last.x > right) {
//            currentIndex--
            val removeLast = recycleView.removeLast()
            removeLast.x = recycleView.first.x - recycleView.first.width
            recycleView.add(0, removeLast)
        }
    }

    private fun slideAnimator(fl: Float) {
        recycleView.forEach {
            it.animate().xBy(fl).start()
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
//        width = r - l
        viewWidth = r - l
        var offset = -viewWidth
        ImageViewA.layout(offset, t, offset + viewWidth, b)
        offset += viewWidth
        ImageViewB.layout(offset, t, offset + viewWidth, b)
        offset += viewWidth
        ImageViewC.layout(offset, t, offset + viewWidth, b)
        offset += viewWidth
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
//        super.onTouchEvent(event)
        detector.onTouchEvent(event)
        val currentView = recycleView.get(1)
//        Log.d("x=${currentView.x}")

        when (event.action) {
            MotionEvent.ACTION_UP -> {
                slideAnimator(when {
                    velocity < -1000 -> { //slide to right
                        currentIndex++
                        -viewWidth.toFloat() - currentView.x
                    }

                    velocity > 1000 -> {
                        //slide to left
                        currentIndex--
                        viewWidth.toFloat() - currentView.x
                    }

                    else -> -currentView.x
                })    //mid
            }
        }
        return true
    }
}