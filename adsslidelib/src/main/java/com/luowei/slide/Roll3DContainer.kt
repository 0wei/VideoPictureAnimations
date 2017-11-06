package com.luowei.slide

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*

/**
 * Created by luowei on 2017/11/3.
 */
class Roll3DContainer : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    var currentBitmap: Bitmap? = null
        get() {
            if (field == null) return null
            if (field?.width != width || field?.height != height)
                field = Bitmap.createScaledBitmap(field, width, height, true)
            return field
        }
        set(value) {
            field = value
            postInvalidate()
        }

    var nextBitmap: Bitmap? = null
        set(value) {
            field = value
            startAnimation()
        }
        get() {
            if (field == null) return null
            if (field?.width != width || field?.height != height)
                field = Bitmap.createScaledBitmap(field, width, height, true)
            return field
        }
    private var currentValue = 0
        set(value) {
            field = value
            invalidate()
        }
    private var valueAnimator: ValueAnimator? = null

    private fun startAnimation() {
        currentValue = 0
        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofInt(0, 360)
        valueAnimator!!.duration = 1000
        valueAnimator!!.addUpdateListener(updateListener)
        valueAnimator!!.addListener(toPreAnimListener)
        valueAnimator!!.start()
    }

    private val updateListener = ValueAnimator.AnimatorUpdateListener { valueAnimator ->
        val value = valueAnimator.animatedValue as Int
//        Log.d("value=$value")
        currentValue = value

    }
    lateinit var listener: AnimatorListenerAdapter
    private val toPreAnimListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            animationMode = if (animationMode == 0) 1 else 0
            listener.onAnimationEnd(animation)
        }
    }

    private val camera = Camera()
    private val bitMatrix = Matrix()

    private val BASE_COUNT = 10
    private var animationMode = if (Random().nextBoolean()) 0 else 1
    override fun onDraw(canvas: Canvas) {
//        Log.d("currentValue=$currentValue")
        if (animationMode == 0) {
            rollInTurnVertical(canvas)
        } else {
            rollInTurnHorizontal(canvas)
        }
//        pixSectorHorizontal(canvas)
//        rote2(canvas)
//        currentValue++
//        postInvalidate()
    }


    private val BASE_DEGREE = 30f

    private fun rollInTurnVertical(canvas: Canvas) {
//        currentBitmap ?: return
//        nextBitmap ?: return
        val size = width / BASE_COUNT.toFloat()
        var left = 0
        var right = 0f
        for (i in 0..BASE_COUNT) {
            var tDegree = currentValue - i * BASE_DEGREE
            if (tDegree < 0)
                tDegree = 0f
            if (tDegree > 90)
                tDegree = 90f
            camera.save()

            camera.rotateX((-tDegree))
            camera.getMatrix(bitMatrix)

            camera.restore()
            canvas.save()
            var tAxisY = tDegree / 90f * height
            if (tAxisY > height)
                tAxisY = height.toFloat()
            if (tAxisY < 0)
                tAxisY = 0f

            bitMatrix.preTranslate(-size / 2f, 0f)
            bitMatrix.postTranslate(size / 2f + left, tAxisY)
            canvas.concat(bitMatrix)
            right += size
            val rect = Rect(left, 0, right.toInt(), height)
            if (currentBitmap != null) {
                canvas.drawBitmap(currentBitmap, rect, Rect(0, 0, rect.width(), height), null)
            }
            canvas.restore()


            camera.save()
            camera.rotateX((90 - tDegree))
            camera.getMatrix(bitMatrix)
            camera.restore()
            canvas.save()
            bitMatrix.preTranslate(-size / 2f, -height.toFloat())
            bitMatrix.postTranslate(size / 2f + left, tAxisY)
            canvas.concat(bitMatrix)
            if (nextBitmap != null) {

                canvas.drawBitmap(nextBitmap, rect, Rect(0, 0, rect.width(), height), null)
            }
            canvas.restore()
            left = right.toInt()
        }
    }

    private fun rollInTurnHorizontal(canvas: Canvas) {
//        currentBitmap ?: return
//        nextBitmap ?: return
        val size = height / BASE_COUNT.toFloat()
        var top = 0
        var bottom = 0f
        for (i in 0..BASE_COUNT) {
            var tDegree = currentValue - i * BASE_DEGREE
            if (tDegree < 0)
                tDegree = 0f
            if (tDegree > 90)
                tDegree = 90f
            var tAxisX = tDegree / 90f * width
            if (tAxisX > width)
                tAxisX = width.toFloat()
            if (tAxisX < 0)
                tAxisX = 0f

            camera.save()
            camera.rotateY(tDegree)
            camera.getMatrix(bitMatrix)
            camera.restore()

            canvas.save()
            bitMatrix.preTranslate(0f, -size / 2f)
            bitMatrix.postTranslate(tAxisX, size / 2f + top)
            canvas.concat(bitMatrix)
            bottom += size
            val rect = Rect(0, top, width, bottom.toInt())
            if (currentBitmap != null) {
                canvas.drawBitmap(currentBitmap, rect, Rect(0, 0, width, rect.height()), null)
            }
            canvas.restore()


            camera.save()
            camera.rotateY((tDegree - 90))
            camera.getMatrix(bitMatrix)
            camera.restore()

            canvas.save()
            bitMatrix.preTranslate(-width.toFloat(), -size / 2f)
            bitMatrix.postTranslate(tAxisX, size / 2f + top)
            canvas.concat(bitMatrix)
            if (nextBitmap != null) {
                canvas.drawBitmap(nextBitmap, rect, Rect(0, 0, width, rect.height()), null)
            }
            canvas.restore()

            top = bottom.toInt()
        }
    }


    private fun pixSectorHorizontal(canvas: Canvas) {
        canvas.drawBitmap(nextBitmap, 0f, 0f, null)
    }

    fun setProgress(progress: Int) {
        currentValue = progress
        invalidate()
    }
}