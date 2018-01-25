package com.luowei.slide

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.unistrong.luowei.adsslidelib.R
import com.unistrong.luowei.commlib.Log

/**
 * Created by luowei on 2017/12/16.
 */
class CircleIndicator : LinearLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private val textView = TextView(context)
    private var count=0

    init {
        orientation = HORIZONTAL
        textView.setTextColor(Color.WHITE)
//        setPadding(20, 20, 20, 20)
//        addView(textView)
    }


    private var currentIndex = 0
    private val scale = .7f
    private val margin = 5
    fun updateIndicator(count: Int) {
        if (count + childCount < 20) {
            removeView(textView)
            if (childCount < count) {
                for (i in 0 until count - childCount) {
//                    Log.d("i = $i  count= $count")
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
        } else {
            removeAllViews()
            addView(textView)
        }
        this.count=count
    }

    fun onPageSelected(currentIndex: Int) {
        if (this.currentIndex != currentIndex && currentIndex < childCount) {
            getChildAt(currentIndex)?.animate()?.scaleX(1f)?.scaleY(1f)?.start()
            getChildAt(this.currentIndex)?.animate()?.scaleX(scale)?.scaleY(scale)?.start()
            this.currentIndex = currentIndex

        } else {
            textView.text = "$currentIndex - $count"
        }
    }
}