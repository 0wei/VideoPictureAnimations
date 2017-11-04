package com.luowei.slide

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.unistrong.luowei.adsslidelib.R
import com.unistrong.luowei.commlib.Log

/**
 * Created by luowei on 2017/11/3.
 */
class Roll3DContainer : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    val bitmap by lazy {
        BitmapFactory.decodeResource(resources, R.drawable.abc).let {
            Bitmap.createScaledBitmap(it, width, height, true)
        }
    }
    val camera = Camera()
    val bitMatrix = Matrix()
    val rect by lazy { Rect(0, 0, 300, height) }
    var degree = 0
    override fun onDraw(canvas: Canvas) {
//        if (degree < 0)
//            degree = 0
//        if (degree > 90)
//            degree = 0
        rote1(canvas)
//        rote2(canvas)
        degree++
//        postInvalidate()
    }


    private fun drawMath() {

    }

    private fun radio(tDegree: Float): Double {
        return tDegree / 180 * Math.PI
    }

    private fun rote1(canvas: Canvas) {
        val pw = width / 10f
        Log.d("pw=$pw")
        var left = 0
        var right = 0f
        for (i in 0..10) {
            var tDegree = degree - i * 30f
            if (tDegree < 0)
                tDegree = 0f
            if (tDegree > 90)
                tDegree = 90f
            camera.save()
//            camera.translate(0f, 0f,-cameraY.toFloat())
            camera.rotateX((-tDegree))
            camera.getMatrix(bitMatrix)
//            camera.setLocation(0f,0f,-20f)
            camera.restore()
            canvas.save()
            var tAxisY = tDegree / 90f * bitmap.height
            if (tAxisY > bitmap.height)
                tAxisY = bitmap.height.toFloat()
            if (tAxisY < 0)
                tAxisY = 0f

            bitMatrix.preTranslate(-pw / 2f, 0f)
            bitMatrix.postTranslate(pw / 2f + left, tAxisY)
            canvas.concat(bitMatrix)

            right += pw
            val rect = Rect(left, 0, right.toInt(), height)
            Log.d("[$left, ${right.toInt()}]")


            canvas.drawBitmap(bitmap, rect, Rect(0, 0, rect.width(), height), null)
            canvas.restore()


            camera.save()
            camera.rotateX((90 - tDegree).toFloat())
            camera.getMatrix(bitMatrix)
            camera.restore()
            canvas.save()
            bitMatrix.preTranslate(-pw / 2f, -height.toFloat())
            bitMatrix.postTranslate(pw / 2f + left, tAxisY)
            canvas.concat(bitMatrix)
            canvas.drawBitmap(bitmap, rect, Rect(0, 0, rect.width(), height), null)
            canvas.restore()
            left = right.toInt()
        }
    }

    var posDy = 0
        set(value) {
            field = value
            postInvalidate()
        }
    var cameraY = 0
        set(value) {
            field = value
            postInvalidate()
        }

    private fun rote2(canvas: Canvas) {
        val pw = width / 10f
        for (i in 0..10) {

            var tDegree = degree - i * 90f / 10f
            if (tDegree < 0)
                tDegree = 0f
            if (tDegree > 90)
                tDegree = 90f


            camera.save()
            camera.rotateX((90 - tDegree).toFloat())

            camera.getMatrix(bitMatrix)
            camera.restore()
            canvas.save()
            var tAxisY = tDegree / 90f * height
            if (tAxisY > bitmap.height)
                tAxisY = bitmap.height.toFloat()
            if (tAxisY < 0)
                tAxisY = 0f

            bitMatrix.preTranslate(-pw / 2f, -height.toFloat())
            bitMatrix.postTranslate(pw / 2f + pw * i, tAxisY)

//            bitMatrix.preTranslate(-pw / 2f, -height.toFloat())
//            bitMatrix.postTranslate(-pw / 2f, tAxisY)
            canvas.concat(bitMatrix)
            val rect = Rect((pw * i).toInt(), 0, (pw * i + pw).toInt(), height)
            canvas.drawBitmap(bitmap, rect, Rect(0, 0, pw.toInt(), height), null)
            canvas.restore()
        }
    }

    fun setProgress(progress: Int) {
        degree = progress
        invalidate()
    }
}