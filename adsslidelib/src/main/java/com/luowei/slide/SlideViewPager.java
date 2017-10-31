package com.luowei.slide;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;


import com.unistrong.luowei.commlib.Log;

import java.lang.ref.WeakReference;

/**
 * Created by LuoWei on 2016/8/3.
 */
public class SlideViewPager extends ViewPager implements ISlide {

    private static final boolean DEBUG = false;
    private MyHandler handler;
    private int timeOut = 5000;
    //    private int timeOut = 500;
    private boolean canSlide = true;
    private boolean isAutoSlideRun;  //当前是否需要重新启动滑动
    private PlayListener replayListener;
    
    @Override
    public boolean requestSlideNext() {
        clearSlide();
        return slideNext(true);
    }

    public void clearSlide() {
        if (handler != null)
            handler.removeMessages(0);
    }

    public void setReplayListener(PlayListener replayListener) {
        this.replayListener = replayListener;
    }


    private static class MyHandler extends Handler {
        WeakReference<SlideViewPager> pager;

        MyHandler(SlideViewPager pager) {
            this.pager = new WeakReference<>(pager);
        }

        @Override
        public void handleMessage(Message msg) {
            SlideViewPager slideViewPager = pager.get();
            if (slideViewPager != null) {
                if(DEBUG)Log.INSTANCE.w("slideNext ");
                slideViewPager.slideNext();
            } else {
                if(DEBUG)Log.INSTANCE.w("slide is null");
            }

        }
    }


    public SlideViewPager(Context context) {
        this(context, null);
    }


    public SlideViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(DEBUG)Log.INSTANCE.d("selected " + position);
                slideDelay();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                canSlide = state == ViewPager.SCROLL_STATE_IDLE;
            }
        });
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        slideDelay();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearSlide();
        if(DEBUG)Log.INSTANCE.w("onDetachedFromWindow, set handler = null");
        handler = null;
        replayListener = null;  //// TODO: 2016/10/17 不置为空,会导致创建一个新的该对象此处有应用
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (!(adapter instanceof SlideAdapter)) {
            throw new IllegalArgumentException("just SlideAdapter support");
        }
        super.setAdapter(adapter);
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                if (!isAutoSlideRun) {
                    isAutoSlideRun = true;
                    slideDelay();
                }
            }
        });
    }

    private void slideDelay() {
        if (handler == null) {
            handler = new MyHandler(this);
            if(DEBUG)Log.INSTANCE.e("handle is null, and create");
        }
        handler.removeMessages(0);
        handler.sendEmptyMessageDelayed(0, timeOut);

    }

    private void slideNext() {
        slideNext(false);
    }

    private boolean slideNext(boolean force) {
        int count = getAdapter().getCount();
        if (count < 2) {
            isAutoSlideRun = false;
            if (replayListener != null) {
                replayListener.replay();
            }
            if(DEBUG)Log.INSTANCE.e("count < 2");
            return false;
        }
        int currentItem = getCurrentItem();
        //// TODO: 2016/10/14 此处被删除导致类型和显示的对不上,需要获取到当前界面显示的fragment
        Fragment item1;// = ((SlideAdapter) getAdapter()).getItem(currentItem); //此处item变化导致不是当前显示的Fragment
//        if (DEBUG) Logger.getLogger().d("current slide Item : %d. is image : %B", currentItem,item1 instanceof ImageShowFragment);
        item1 = ((SlideAdapter) getAdapter()).getCurrentFragment();
        if (item1 == null) {
            if(DEBUG)Log.INSTANCE.e("item is null");
            return false;
        }
        ISlide.SlideItem item = (ISlide.SlideItem) item1;
        if (!canSlide) {    //触屏禁止自动滑动
            slideDelay();
            if(DEBUG)Log.INSTANCE.e("can't slide ,wait moment");
            return false;
        }
        if (!force) {
            if ((!item.canSlide())) {
                return false;
            }
        }
        currentItem = currentItem + 1;
        if (currentItem >= ((SlideAdapter) getAdapter()).getPlaylist().size()) {
            if (replayListener != null) {
                replayListener.replay();
            }
        }
        currentItem = currentItem >= count ? 0 : currentItem;
        if(DEBUG)Log.INSTANCE.d("slideNext to " + currentItem);
        setCurrentItem(currentItem);
        return true;
    }

    /**
     * @param timeMillis millis
     */
    public void setTimeout(int timeMillis) {
        this.timeOut = timeMillis;
        if (handler != null) {
            handler.removeMessages(0);
            handler.sendEmptyMessageDelayed(0, timeMillis);
        }
    }

    /**
     * 播放列表结束回调
     */
    public interface PlayListener {
        void replay();
    }


}
