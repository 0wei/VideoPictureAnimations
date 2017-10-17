package com.luowei.slide;

/**
 * Created by LuoWei on 2016/8/3.
 * 自动滑动
 */
public interface ISlide {

    /**
     * 请求滑动到下一页
     */
    boolean requestSlideNext();


    interface SlideItem {
        /**
         * @return 当前页面是否支持自动滑动
         * 若返回<code>false</code>则需要调用{@linkplain #requestSlideNext()}来请求激活自动滑动,否则将一直处于当前位置
         */
        boolean canSlide();

        /**
         * 设置滑动父类实现
         */
        void setSlide(ISlide slide);


    }
}
