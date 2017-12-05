package com.luowei.slide

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.ViewGroup


import com.unistrong.luowei.commlib.Log

import java.util.ArrayList

/**
 * Created by LuoWei on 2016/8/3.
 */
class SlideAdapter(fm: FragmentManager, private val viewPager: ISlide) : FragmentStatePagerAdapter(fm) {
    val playlist = ArrayList<Item>()

    private fun ArrayList<Item>.getRecycleItem(position: Int): Item {
        return get(position % size)
    }

    private var defaultItem: Item? = null
    private var addDefault: Item? = null

    var currentFragment: Fragment? = null
        private set   //当前显示的fragment

    fun addItem(item: Item) {
        if (playlist.indexOf(item) == -1) {
            playlist.add(item)
            if (DEBUG) {
                printItems()
            }
            notifyDataSetChanged()
        }
    }


    fun setDefault(item: Item) {
        playlist.remove(defaultItem)
        defaultItem = item
        notifyDataSetChanged()
    }

    override fun setPrimaryItem(container: ViewGroup?, position: Int, objecz: Any) {
        super.setPrimaryItem(container, position, objecz)
        val fragment = objecz as Fragment
        currentFragment = fragment

    }

    private fun printItems() {
        val builder = StringBuilder()
        var count = 0
        builder.append("\n")
        for (item in playlist) {
            builder.append(count++).append(",").append(item.type).append(",").append(item.path).append("\n")
        }
    }

    internal fun removeItem(position: Int) {
        //        playlist.set(position, null);
        playlist.removeAt(position)
        if (DEBUG) {
            printItems()
        }
        notifyDataSetChanged()
    }

    //    public void addAllItem(Collection<Item> items) {
    //        playlist.addAll(items);
    //        notifyDataSetChanged();
    //    }

    fun clear() {
        playlist.clear()
        notifyDataSetChanged()
    }


    override fun getItem(pt: Int): Fragment? {
//        var position = pt
//        position %= playlist.size
        val item = playlist.getRecycleItem(pt)

        val t = playlist.getRecycleItem(pt + 1)

        val next = if (t.type == ItemType.Image) {
            t.path
        } else {
            t.videoImage
        }

        var fragment: Fragment? = null

        run {
            if (item.type == ItemType.Video) {
                val videoFragment = VideoFragment.create(item.path,item.videoImage,next)
                videoFragment.setSlide(viewPager)
                fragment = videoFragment
            } else if (item.type == ItemType.Image) {
                val images = ArrayList<String>()
                images.add(item.path)
                if(next!=null){
                    images.add(next)
                }
                val imageShowFragment = ImageShowFragment.create(images = images.toTypedArray())
                imageShowFragment.setSlide(viewPager)
                fragment = imageShowFragment
            }
        }
        return fragment
    }


    override fun getItemPosition(objecz: Any): Int {
        var path: String? = null
        if (objecz is VideoFragment) {
            path = objecz.videoPath
        } else if (objecz is ImageShowFragment) {
            path = objecz.imagePath
        }
        val currentItem = (viewPager as ViewPager).currentItem
        if (currentItem != -1 && playlist.size > 0) {
            for (item in playlist) {
                if (item.path == path) {   //是否已经被删除
                    return PagerAdapter.POSITION_UNCHANGED
                }
            }
        }
        return PagerAdapter.POSITION_NONE   //已经被删除

    }

    override fun getCount(): Int {
        if (playlist.size == 0 && defaultItem != null) {
            playlist.add(defaultItem!!)
            addDefault = defaultItem
        }
        if (playlist.size >= 2 && addDefault != null) {
            if (DEBUG) Log.d("remove default image")
            playlist.remove(addDefault!!)
            addDefault = null
        }
        val count = playlist.size
        return if (count < 2) count else Integer.MAX_VALUE
    }


    enum class ItemType {
        Video, Image
    }

    class Item(internal var type: ItemType, internal var path: String, var videoImage: String? = null) {
        private val fragment: Fragment? = null

        override fun equals(o: Any?): Boolean {
            return o is Item && o.path == path
        }
    }

    companion object {
        private val DEBUG = false
    }


}
