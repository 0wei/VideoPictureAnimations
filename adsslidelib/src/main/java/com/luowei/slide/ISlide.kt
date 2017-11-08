package com.luowei.slide

/**
 * Created by LuoWei on 2016/8/3.
 * 自动滑动
 */
interface ISlide {

    /**
     * 请求滑动到下一页
     * @param force true ignore canSlide result
     */
    fun requestSlideNext(force:Boolean=false,animator:Boolean=true): Boolean


    interface SlideItem {
        /**
         * @return 当前页面是否可以自动滑动
         * 若返回`false`则需要调用[.requestSlideNext]来请求激活自动滑动,否则将一直处于当前位置
         */
        fun canSlide(): Boolean

        /**
         * 设置滑动父类实现
         */
        fun setSlide(slide: ISlide)


    }
}
