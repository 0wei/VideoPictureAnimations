package com.luowei.slide


import android.content.Context
import android.database.DataSetObserver
import android.os.Handler
import android.os.Message
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import com.unistrong.luowei.commlib.Log
import java.lang.ref.WeakReference

/**
 * Created by LuoWei on 2016/8/3.
 */
class SlideViewPager : ViewPager, ISlide {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var timerHandler: Handler? = null
    private var timeOut = 5000
    var scrollIdle = true
    private var isAutoSlideRun: Boolean = false  //当前是否需要重新启动滑动
    private var replayListener: PlayListener? = null


    override fun requestSlideNext(force: Boolean, animator: Boolean): Boolean {
        clearSlide()
        return slideNext(force, animator)
    }

    private fun clearSlide() {
        if (timerHandler != null)
            timerHandler!!.removeMessages(0)
    }

    fun setReplayListener(replayListener: PlayListener) {
        this.replayListener = replayListener
    }


    internal class MyHandler(pager: SlideViewPager) : Handler() {
        var pager: WeakReference<SlideViewPager>

        init {
            this.pager = WeakReference(pager)
        }

        override fun handleMessage(msg: Message) {
            val slideViewPager = pager.get()
            if (slideViewPager != null) {
                slideViewPager.slideNext()
            } else {
                if (DEBUG) Log.w("slide is null")
            }

        }
    }


    init {
        init()
    }

    private fun init() {
        addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (DEBUG) Log.d("selected " + position)
                slideDelay()
            }

            override fun onPageScrollStateChanged(state: Int) {
                scrollIdle = state == ViewPager.SCROLL_STATE_IDLE
            }
        })
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        slideDelay()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clearSlide()
        if (DEBUG) Log.w("onDetachedFromWindow, set timerHandler = null")
        timerHandler = null
        replayListener = null  //// TODO: 2016/10/17 不置为空,会导致创建一个新的该对象此处有应用
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        if (adapter !is SlideAdapter) {
            throw IllegalArgumentException("just SlideAdapter support")
        }
        super.setAdapter(adapter)
        adapter.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                if (!isAutoSlideRun) {
                    isAutoSlideRun = true
                    slideDelay()
                }
            }
        })
    }

    private fun slideDelay() {
        if (timerHandler == null) {
            timerHandler = MyHandler(this)
            if (DEBUG) Log.e("handle is null, and create")
        }
        if (timerHandler != null) {
            if (DEBUG) Log.d("delay to slide")
            timerHandler!!.removeMessages(0)
            timerHandler!!.sendEmptyMessageDelayed(0, timeOut.toLong())
        }
    }

    private fun slideNext() {
        slideNext(false)
    }

    private fun slideNext(force: Boolean, animator: Boolean = true): Boolean {
        if (!scrollIdle) {    //触屏禁止自动滑动
            slideDelay()
            if (DEBUG) Log.e("can't slide ,wait moment")
            return false
        }
        val count = adapter!!.count
        if (count < 2) {
            isAutoSlideRun = false
            if (replayListener != null) {
                replayListener!!.replay()
            }
            return false
        }
        var currentItem = currentItem
        //// TODO: 2016/10/14 此处被删除导致类型和显示的对不上,需要获取到当前界面显示的fragment
//        val item1: Fragment?// = ((SlideAdapter) getAdapter()).etItem(currentItem); //此处item变化导致不是当前显示的Fragment
        //        if (DEBUG) Logger.getLogger().d("current slide Item : %d. is image : %B", currentItem,item1 instanceof ImageShowFragment);
        val item1 = (adapter as SlideAdapter).currentFragment
        if (item1 == null) {
            if (DEBUG) Log.e("item is null")
            return false
        }
        val item = item1 as ISlide.SlideItem?

        if (!force) {
            if (!item!!.canSlide()) {
                slideDelay()
                return false
            }
        }

        currentItem = currentItem + 1
        if (currentItem >= (adapter as SlideAdapter).playlist.size) {
            if (replayListener != null) {
                replayListener!!.replay()
            }
        }
        currentItem = if (currentItem >= count) 0 else currentItem
        if (DEBUG) Log.d("setCurrentItem=" + currentItem)
        setCurrentItem(currentItem, animator)
        return true
    }

    /**
     * @param timeMillis millis
     */
    fun setTimeout(timeMillis: Int) {
        this.timeOut = timeMillis
        if (timerHandler != null) {
            timerHandler!!.removeMessages(0)
            timerHandler!!.sendEmptyMessageDelayed(0, timeMillis.toLong())
        }
    }

    /**
     * 播放列表结束回调
     */
    interface PlayListener {
        fun replay()
    }

    companion object {

        private val DEBUG = false
    }


}
