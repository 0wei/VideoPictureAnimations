package com.luowei.slide

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.support.annotation.IntRange
import android.util.AttributeSet
import android.view.View
import com.unistrong.luowei.commlib.Log
import java.util.*

/**
 * Created by luowei on 2017/11/3.
 */
class Roll3DContainer : View {

    companion object {
        private val BASE_COUNT = 10
        private val BASE_DEGREE = 30f
        private val DEBUG = false
    }

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
    //[0,100]
    private var currentValue = 0
        set(value) {
            field = value//Math.min(value, 100)
            invalidate()
        }
    private var valueAnimator: ValueAnimator? = null

    private fun startAnimation() {
        currentValue = 0
        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofInt(0, 100)
        valueAnimator!!.duration = 1000
        valueAnimator!!.addUpdateListener(updateListener)
        valueAnimator!!.addListener(toPreAnimListener)
        valueAnimator!!.start()
    }

    private val updateListener = ValueAnimator.AnimatorUpdateListener { valueAnimator ->
        val value = valueAnimator.animatedValue as Int
//        if(DEBUG)Log.d("value=$value")
        currentValue = value

    }
    lateinit var listener: AnimatorListenerAdapter
    private val toPreAnimListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            listener.onAnimationEnd(animation)
        }
    }

    private val camera = Camera()
    private val bitMatrix = Matrix()


    private val rollInTurnVertical = { canvas: Canvas ->
        //        currentBitmap ?: return
//        nextBitmap ?: return
        val percent = 3.90f
        val size = width / BASE_COUNT.toFloat()
        var left = 0
        var right = 0f
        for (i in 0..BASE_COUNT) {
            var tDegree = currentValue * percent - i * BASE_DEGREE
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

    private val rollInTurnHorizontal = { canvas: Canvas ->
        //        currentBitmap ?: return
//        nextBitmap ?: return
//        100 *percent- BASE_COUNT*BASE_DEGREE = 90
//        val percent = (90+300)/100
        val percent = 3.90f
        val size = height / BASE_COUNT.toFloat()
        var top = 0
        var bottom = 0f
        for (i in 0..BASE_COUNT) {
            var tDegree = currentValue * percent - i * BASE_DEGREE
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

    private val rollBlindsHorizontalNest = { canvas: Canvas ->
        rollBlindsHorizontal(canvas, true)
    }
    private val rollBlindsHorizontalDefault = { canvas: Canvas ->
        rollBlindsHorizontal(canvas, false)
    }

    /**
     * roll left to right
     */
    private fun rollBlindsHorizontal(canvas: Canvas, nest: Boolean = true) {
//        BASE_COUNT*180
        val percent = 4.8f    //18度
        //currentValue [0,100]
        //
        //180+300 = 4.8
        //currentValue = 100,
        //last count = 180:
        //100 * percent - 10 * 30 = 180
        //100 *percent- BASE_COUNT*BASE_DEGREE = 180
        //100 * percent = 180 + 300
        val size = width / BASE_COUNT.toFloat()
        var left = 0
        var right = 0f

        for (i in 0..BASE_COUNT) {
            var tDegree = if (nest)
                currentValue * percent - i * BASE_DEGREE
            else currentValue * 1.8f

//            if(DEBUG)Log.d("tDegree=$tDegree")
            if (tDegree > 180) tDegree = 180f
            if (tDegree < 0) tDegree = 0f
            right += size
            val rect = Rect(left, 0, right.toInt(), height)

            val currDegree = tDegree
            if(DEBUG)Log.d("currDegree=$currDegree")
            if (currDegree <= 90) {
                camera.save()

                camera.rotateY(currDegree)
                camera.getMatrix(bitMatrix)
                camera.restore()
                canvas.save()
                bitMatrix.preTranslate(-size / 2f, 0f)
                bitMatrix.postTranslate(size / 2f + left, 0f)
                canvas.concat(bitMatrix)
                if (currentBitmap != null) {
                    canvas.drawBitmap(currentBitmap, rect, Rect(0, 0, rect.width(), rect.height()), null)
                }
                canvas.restore()
            }

            val nextDegree = -180 + tDegree
            if (nextDegree >= -90) {
                camera.save()
                camera.rotateY(nextDegree)
                camera.getMatrix(bitMatrix)
                camera.restore()
                canvas.save()
                bitMatrix.preTranslate(-size / 2f, 0f)
                bitMatrix.postTranslate(size / 2f + left, 0f)
                canvas.concat(bitMatrix)
                if (nextBitmap != null) {
                    canvas.drawBitmap(nextBitmap, rect, Rect(0, 0, rect.width(), rect.height()), null)
                }
                canvas.restore()
            }
            left = right.toInt()
        }
    }


    private val animationsSet = arrayOf(rollInTurnVertical, rollInTurnHorizontal, rollBlindsHorizontalNest,
            rollBlindsHorizontalDefault)

    private var currentAnimation = animationsSet[Random().nextInt(animationsSet.size)]

    override fun onDraw(canvas: Canvas) {
//        if(DEBUG)Log.d("currentValue=$currentValue")
        currentAnimation.invoke(canvas)
//        rollInTurnVertical(canvas)
//        rollInTurnHorizontal(canvas)
//        rollBlindsHorizontal(canvas)
//        pixSectorHorizontal(canvas)
//        postInvalidate()
    }


    private fun pixSectorHorizontal(canvas: Canvas) {
        canvas.drawBitmap(nextBitmap, 0f, 0f, null)
    }

    fun setProgress(@IntRange(from = 0, to = 100) progress: Int) {
        currentValue = progress
        invalidate()
    }
}