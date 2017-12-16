package com.luowei.slide

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import com.unistrong.luowei.adsslidelib.R
import com.unistrong.luowei.commlib.Log

/**
 * Created by luowei on 2017/12/16.
 */
class CircleIndicator(context: Context?) : LinearLayout(context) {

    init {
        orientation = HORIZONTAL
//        setPadding(20, 20, 20, 20)
    }


    private var currentIndex = 0
    private val scale = .7f
    private val margin = 5
    fun updateIndicator(count: Int) {
        if (childCount < count) {
            for (i in 0 until count - childCount) {
                Log.d("i = $i")
                val view = ImageView(context)
                view.setImageResource(R.drawable.white_radius)
                addView(view)
                view.scaleX = scale
                view.scaleY = scale
                val layoutParams = view.layoutParams as LinearLayout.LayoutParams
//                layoutParams.width = 30
//                layoutParams.height = 30
                layoutParams.leftMargin = margin
                layoutParams.rightMargin = margin

            }
        } else {
            for (i in 0 until childCount - count) {
                removeViewAt(0)
            }
        }
    }

    fun onPageSelected(currentIndex: Int) {
        if (this.currentIndex != currentIndex) {
            getChildAt(currentIndex)?.animate()?.scaleX(1f)?.scaleY(1f)?.start()
            getChildAt(this.currentIndex)?.animate()?.scaleX(scale)?.scaleY(scale)?.start()
            this.currentIndex = currentIndex
        }
    }
}