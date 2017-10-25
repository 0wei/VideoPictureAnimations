package com.luowei.slide;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;


import com.unistrong.luowei.commlib.Log;

import java.util.ArrayList;

/**
 * Created by LuoWei on 2016/8/3.
 */
public class SlideAdapter extends FragmentStatePagerAdapter {
    private static final boolean DEBUG = true;
    private ArrayList<Item> playlist = new ArrayList<>();

    private ISlide viewPager;
    private Item defaultItem;

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    private Fragment currentFragment;   //当前显示的fragment

    ArrayList<Item> getPlaylist() {
        return playlist;
    }

    public void addItem(Item item) {
        if (playlist.indexOf(item) == -1) {
            playlist.add(item);
            if (DEBUG) {
                printItems();
            }
            notifyDataSetChanged();
        }
    }

    public void setDefault(Item item) {
        playlist.remove(defaultItem);
        defaultItem = item;
        notifyDataSetChanged();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        Fragment fragment = (Fragment) object;
        currentFragment = fragment;

    }

    private void printItems() {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        builder.append("\n");
        for (Item item : playlist) {
            builder.append(count++).append(",").append(item.type).append(",").append(item.path).append("\n");
        }
    }

    void removeItem(int position) {
//        playlist.set(position, null);
        playlist.remove(position);
        if (DEBUG) {
            printItems();
        }
        notifyDataSetChanged();
    }

//    public void addAllItem(Collection<Item> items) {
//        playlist.addAll(items);
//        notifyDataSetChanged();
//    }

    public void clear() {
        playlist.clear();
        notifyDataSetChanged();
    }


    public SlideAdapter(FragmentManager fm, ISlide viewPager) {
        super(fm);
        this.viewPager = viewPager;
    }


    @Override
    public Fragment getItem(int position) {
        position %= playlist.size();
        Item item = playlist.get(position);
//        if (position < playlist.size()) {
//            item = playlist.get(position);
//        } else {
//            item = defaultItem;
//        }

        Fragment fragment = null;
        /*if (item == null) {
            fragment = getItem(position + 1);
        } else */
        {
            if (item.type == ItemType.Video) {
                VideoFragment videoFragment = VideoFragment.Companion.create(item.path);
                videoFragment.setSlide(viewPager);
                fragment = videoFragment;
            } else if (item.type == ItemType.Image) {
                ImageShowFragment imageShowFragment = ImageShowFragment.Companion.create(item.path);
                imageShowFragment.setSlide(viewPager);
                fragment = imageShowFragment;
            }
        }
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        String path = null;
        if (object instanceof VideoFragment) {
            path = ((VideoFragment) object).getVideoPath();
        } else if (object instanceof ImageShowFragment) {
            path = ((ImageShowFragment) object).getImagePath();
        }
        int currentItem = ((ViewPager) viewPager).getCurrentItem();
        if (currentItem != -1 && playlist.size() > 0) {
            for (Item item : playlist) {
                if (item.path.equals(path)) {   //是否已经被删除
                    return POSITION_UNCHANGED;
                }
            }
        }
        return POSITION_NONE;   //已经被删除

    }

    @Override
    public int getCount() {
        if (playlist.size() == 0 && defaultItem != null) {
            playlist.add(defaultItem);
        }
        if (playlist.size() >= 2 && defaultItem != null) {
            Log.INSTANCE.d("remove default image");
            playlist.remove(defaultItem);
            defaultItem = null;
        }
        int count = playlist.size();
        return count < 2 ? count : Integer.MAX_VALUE;
    }


    public enum ItemType {
        Video, Image
    }

    public static class Item {
        ItemType type;
        String path;
        private Fragment fragment;

        public Item(ItemType type, String path) {
            this.type = type;
            this.path = path;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Item && ((Item) o).path.equals(path);
        }
    }


}
