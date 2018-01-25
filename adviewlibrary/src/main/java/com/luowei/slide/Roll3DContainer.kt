package com.luowei.slide

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.support.annotation.IntRange
import android.util.AttributeSet
import android.view.View
import com.bumptech.glide.Glide
import com.unistrong.luowei.commlib.Log
import java.util.*

/**
 * Created by luowei on 2017/11/3.
 */
class Roll3DContainer : View {

    companion object {
        private val DEBUG = false
        private var index = 0
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)


    var currentBitmap: Bitmap? = null
//        get() {
//            if (field == null) return null
//            if ((field?.width != width || field?.height != height) && width > 0 && height > 0) {
//                val t = field!!
//                field = Bitmap.createScaledBitmap(t, width, height, true)
//                t.recycle()
////                field = Glide.with(this).asBitmap().load(field).preload()
////                        .submit(width, height).get()
//            }
//            return field
//        }
        set(value) {
            field = value
            if (field != null && field!!.isMutable && (field?.width != width || field?.height != height) && width > 0 && height > 0) {
                field = Bitmap.createScaledBitmap(field, width, height, false)
            }
            currentValue = 0
            invalidate()
//            postInvalidate()
        }

    var nextBitmap: Bitmap? = null
        set(value) {
            currentBitmap = field
            field = value
            if (field != null && field!!.isMutable && (field?.width != width || field?.height != height) && width > 0 && height > 0) {
                field = Bitmap.createScaledBitmap(field, width, height, false)

            }
            val nextInt = Random().nextInt(animationsSet.size)
//            val nextInt = index++ % animationsSet.size
            currentAnimation = animationsSet[nextInt]
//            Log.d("animation= $nextInt")
            startAnimation()
        }
//        get() {
//            if (field == null) return null
//            if ((field?.width != width || field?.height != height) && width > 0 && height > 0) {
//                val t = field!!
//                field = Bitmap.createScaledBitmap(t, width, height, true)
//                t.recycle()
//                field = Glide.with(this).asBitmap().load(field).submit(width, height).get()
//            }
//            return field
//        }
    //[0,100]
    private var currentValue = 0
        set(value) {
            field = value//Math.min(value, 100)
            invalidate()
        }
    private var valueAnimator: ValueAnimator? = null

    private val paint = Paint()
    private fun startAnimation() {
        currentValue = 0
        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofInt(0, 100)
        valueAnimator!!.duration = 1000
//        valueAnimator!!.interpolator = DecelerateInterpolator()
        valueAnimator!!.addUpdateListener(updateListener)
        valueAnimator!!.addListener(toPreAnimListener)
        valueAnimator!!.start()
    }

    private val updateListener = ValueAnimator.AnimatorUpdateListener { valueAnimator ->
        val value = valueAnimator.animatedValue as Int
//        if(DEBUG)Log.d("value=$value")
        currentValue = value

    }
    var listener: AnimatorListenerAdapter? = null
    private val toPreAnimListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            currentBitmap = Bitmap.createBitmap(nextBitmap)
            listener?.onAnimationEnd(animation)
        }
    }

    private val camera = Camera()
    private val bitMatrix = Matrix()

    private val rollInTurnVertical = { canvas: Canvas ->
        val count = 10
        val degree = 30
        val percent = percent(count, degree, 90)
        val size = width / count.toFloat()
        var left = 0
        var right = 0f
        for (i in 0..count) {
            var tDegree = currentValue * percent - i * degree.toFloat()
            if (tDegree < 0) tDegree = 0f
            if (tDegree > 90) tDegree = 90f
            var tAxisY = tDegree / 90f * height
            if (tAxisY > height) tAxisY = height.toFloat()
            if (tAxisY < 0) tAxisY = 0f
            right += size
            val rect = Rect(left, 0, right.toInt(), height)
            val dstRect = Rect(0, 0, rect.width(), height)

            if (currentBitmap != null) {
                camera.save()
                camera.rotateX((-tDegree))
                camera.getMatrix(bitMatrix)
                camera.restore()
                bitMatrix.preTranslate(-size / 2f, 0f)
                bitMatrix.postTranslate(size / 2f + left, tAxisY)


                canvas.save()
                canvas.concat(bitMatrix)
                canvas.drawBitmap(currentBitmap, rect, dstRect, null)
                canvas.restore()
            }


            if (nextBitmap != null) {
                camera.save()
                camera.rotateX((90 - tDegree))
                camera.getMatrix(bitMatrix)
                camera.restore()
                canvas.save()
                bitMatrix.preTranslate(-size / 2f, -height.toFloat())
                bitMatrix.postTranslate(size / 2f + left, tAxisY)


                canvas.concat(bitMatrix)
                canvas.drawBitmap(nextBitmap, rect, dstRect, null)
                canvas.restore()
            }
            left = right.toInt()
        }
    }

    private val rollInTurnHorizontal = { canvas: Canvas ->
        //        currentBitmap ?: return
//        nextBitmap ?: return
//        100 *percent- count*degree = 90
//        val percent = (90+300)/100
        val count = 10
        val degree = 30
        val percent = percent(count, degree, 90)
        val size = height / count.toFloat()
        var top = 0
        var bottom = 0f
        for (i in 0..count) {
            var tDegree = currentValue * percent - i * degree.toFloat()
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
            val dstRect = Rect(0, 0, width, rect.height())
            if (currentBitmap != null) {
                canvas.drawBitmap(currentBitmap, rect, dstRect, null)
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
                canvas.drawBitmap(nextBitmap, rect, dstRect, null)
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
//        count*180
        val count = 15
        val degree = 40
        val percent = percent(count, degree, 180)    //18度
        //currentValue [0,100]
        //
        //180+300 = 4.8
        //currentValue = 100,
        //last count = 180:
        //100 * percent - 10 * 30 = 180
        //100 *percent- count*degree = 180
        //100 * percent = 180 + 300
        val size = width / count.toFloat()
        var left = 0
        var right = 0f

        for (i in 0..count) {
            var tDegree = if (nest)
                currentValue * percent - i * degree.toFloat()
            else currentValue * 1.8f
//            if(DEBUG)Log.d("tDegree=$tDegree")
            if (tDegree > 180) tDegree = 180f
            if (tDegree < 0) tDegree = 0f
            right += size
            val srcRect = Rect(left, 0, right.toInt(), height)
            val dstRect = Rect(0, 0, srcRect.width(), srcRect.height())
            val currDegree = tDegree
            if (DEBUG) Log.d("currDegree=$currDegree")
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
                    canvas.drawBitmap(currentBitmap, srcRect, dstRect, null)
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
                    canvas.drawBitmap(nextBitmap, srcRect, dstRect, null)
                }
                canvas.restore()
            }
            left = right.toInt()
        }
    }


    private val fade = { canvas: Canvas ->
        if (currentBitmap != null) {
            paint.alpha = ((100 - currentValue) * 2.55).toInt()
            val matrix = Matrix()

//            matrix.postScale(width / currentBitmap!!.width.toFloat(), height / currentBitmap!!.height.toFloat(), width / 2f, height / 2f)
            matrix.postScale(width / currentBitmap!!.width.toFloat(), height / currentBitmap!!.height.toFloat())
//            canvas.drawBitmap(currentBitmap, 0f, 0f, paint)
            canvas.drawBitmap(currentBitmap, matrix, paint)
            paint.alpha=255
        }
        if (nextBitmap != null) {
            paint.alpha = ((currentValue) * 2.55).toInt()
            val matrix = Matrix()
            matrix.postScale(width / nextBitmap!!.width.toFloat(), height / nextBitmap!!.height.toFloat())
//            canvas.drawBitmap(nextBitmap, 0f, 0f, paint)
            canvas.drawBitmap(nextBitmap, matrix, paint)
            paint.alpha=255
        }
    }

    private val slideRight2Left = { canvas: Canvas ->
//        canvas.drawColor(Color.RED)
        if (currentBitmap != null) {
//            val matrix = Matrix()
//            val bitmap = currentBitmap
//            matrix.postScale(width / bitmap!!.width.toFloat(), height / bitmap!!.height.toFloat())
//            canvas.save()
//            canvas.concat(matrix)
//            canvas.drawBitmap(currentBitmap, -bitmap!!.width * currentValue / 100f, 0f, paint)
//            canvas.restore()
            canvas.drawBitmap(currentBitmap, -width * currentValue / 100f/2, 0f, paint)

        }
        if (nextBitmap != null) {
//            val matrix = Matrix()
//            val bitmap = nextBitmap
//            matrix.postScale(width / bitmap!!.width.toFloat(), height / bitmap!!.height.toFloat())
//            canvas.save()
//            canvas.concat(matrix)
//            canvas.drawBitmap(nextBitmap, bitmap!!.width * (100 - currentValue) / 100f, 0f, paint)
//            canvas.restore()
            canvas.drawBitmap(nextBitmap, width * (100 - currentValue) / 100f, 0f, paint)

        }
    }


    private fun percent(count: Int, step: Int, result: Int): Float {
        return (result + count * step) / 100f
    }

    private val slideVertical = { canvas: Canvas ->
        slide(canvas, true)
    }
    private val slideVerticalInverse = { canvas: Canvas ->
        slide(canvas, false)
    }

    private fun slide(canvas: Canvas, slideCurrentBmp: Boolean = true) {
        val BASE_COUNT = 10
        val first: Bitmap?
        val secod: Bitmap?
        if (slideCurrentBmp) {
            first = nextBitmap
            secod = currentBitmap
        } else {
            first = currentBitmap
            secod = nextBitmap
        }

        if (first != null) {
            canvas.drawBitmap(first, 0f, 0f, null)
        }
        val size = width / BASE_COUNT.toFloat()
        var left = 0
        var right = 0f
        val baseHeight = height / 2
        val percent = percent(BASE_COUNT, baseHeight, height)
        //100
        for (i in 0..BASE_COUNT) {
            right += size
            var top = if (!slideCurrentBmp) {
                (100 - currentValue) * percent - i * baseHeight
            } else (currentValue) * percent - i * baseHeight
            if (top < 0) top = 0f
            val srcRect = Rect(left, 0, right.toInt(), height)
            val dstRect = Rect(left, 0, right.toInt(), height)
            canvas.save()
            bitMatrix.reset()
            bitMatrix.setTranslate(0f, top.toFloat())
            canvas.concat(bitMatrix)
            if (secod != null) {
                canvas.drawBitmap(secod, srcRect, dstRect, null)
            }
            canvas.restore()
            left = right.toInt()
        }
    }

    private val animationsSet = arrayOf(rollInTurnVertical, rollInTurnHorizontal, rollBlindsHorizontalNest,
            rollBlindsHorizontalDefault, fade, slideRight2Left, slideVertical, slideVerticalInverse)
//    private val animationsSet = arrayOf(rollBlindsHorizontalNest)
//    private val animationsSet = arrayOf(slideRight2Left)

    private var currentAnimation = animationsSet[Random().nextInt(animationsSet.size)]

    override fun onDraw(canvas: Canvas) {
        if(DEBUG)Log.d("currentValue=$currentValue")
        currentAnimation.invoke(canvas)

//        rollBlindsHorizontal(canvas,false)
//        rollInTurnHorizontal(canvas)
//        slide(canvas,false)
//        slideVertical(canvas)
//        rollInTurnVertical(canvas)
//        rollInTurnHorizontal(canvas)
//        rollBlindsHorizontal(canvas)
//        pixSectorHorizontal(canvas)
//        postInvalidate()
    }


    fun setProgress(@IntRange(from = 0, to = 100) progress: Int) {
        currentValue = progress
        invalidate()
    }
}