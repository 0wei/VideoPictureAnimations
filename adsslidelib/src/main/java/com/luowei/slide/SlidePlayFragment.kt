package com.luowei.slide


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup



import java.io.File

import com.unistrong.luowei.adsslidelib.R


/**
 * 轮播图
 */
class SlidePlayFragment : Fragment() {


    private var adapter: SlideAdapter? = null
    private var dir: File? = null
    private lateinit var viewPager:SlideViewPager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null /*&& item.path.endsWith(fileName)*/) {
            val path = arguments.getString(BASE_PATH)
            dir = File(path!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.slide_fragment_slide_play, container, false)
        viewPager = view.findViewById(R.id.viewPager_slide_ads) as SlideViewPager
        adapter = SlideAdapter(childFragmentManager, viewPager)
        //        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setPageTransformer(true, DepthPageTransformer())
        viewPager.setAdapter(adapter)
        setAdType(AdType.TYPE_MIXED)
        return view
    }


    /**
     * 加载播放的文件
     */
    fun setAdType(type: AdType) {
        adapter!!.clear()
        when (type) {
            AdType.TYPE_PICTURE -> addItem(AdType.TYPE_PICTURE)
            AdType.TYPE_VIDEO -> addItem(AdType.TYPE_VIDEO)
            AdType.TYPE_MIXED -> {
                addItem(AdType.TYPE_VIDEO)
                addItem(AdType.TYPE_PICTURE)
            }
            else -> {
            }
        }
    }

    private fun addItem(type: AdType) {
        if (dir == null || !dir!!.isDirectory || dir!!.listFiles() == null) return
        for (file in dir!!.listFiles()) {
            if (isVideo(file.name) && type == AdType.TYPE_VIDEO) {
                addVideo(file.absolutePath)
            } else if (isPicture(file.name) && type == AdType.TYPE_PICTURE) {
                addPicture(file.absolutePath)
            }
        }
    }


    fun deleteFile(fileName: String) {
        val playlist = adapter!!.playlist
        var i = 0
        while (i < playlist.size) {
            val item = playlist[i]
            if (item != null) {
                val item1 = item.path.toLowerCase()
                val item2 = fileName.toLowerCase()

                if (item1.endsWith(item2)) {
                    adapter!!.removeItem(i--)
                }
            }
            i++
        }
    }


    /**
     * @param timeOut 秒
     */
    fun setFile(timeOut: Int, files: List<File>) {
        adapter!!.clear()
        viewPager.setTimeout(timeOut * 1000)
        for (name in files) {
            addItem(name)
        }
    }

    fun addItem(file: File) {
        val name = file.name
        if (isVideo(name)) {
            addVideo(file.absolutePath)
        } else if (isPicture(name)) {
            addPicture(file.absolutePath)
        }
    }

    private fun addVideo(absPath: String) {
        adapter!!.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Video, absPath))
    }

    private fun addPicture(absPath: String) {
        adapter!!.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Image, absPath))
    }
    /*private void addVideo(String name) {
        addVideo(name, dir);
    }

    private void addVideo(String name, File dir) {
        ArrayList<String> strings;
        strings = getFileList2(name, dir);
        for (String string : strings) {
            if (DEBUG) Logger.d("add Video: %s", string);
            adapter.addItem(new SlideAdapter.Item(SlideAdapter.ItemType.Video, string));
        }
    }*/


    /* private void addPicture(String name, File dir) {
        ArrayList<String> strings;
        strings = getFileList2(name, dir);
        for (String string : strings) {
            if (DEBUG) Logger.d("add image: %s", string);
            adapter.addItem(new SlideAdapter.Item(SlideAdapter.ItemType.Image, string));
        }
    }

    private ArrayList<String> getFileList2(String name, File dir) {
        ArrayList<File> files = FileUtil.findFiles(name, dir);
        ArrayList<String> f = new ArrayList<>();
        for (File file : files) {
            f.add(file.getAbsolutePath());
        }
        return f;
    }*/


    override fun onDestroyView() {
        super.onDestroyView()

    }

    companion object {
        private val DEBUG = false
        private val BASE_PATH = "SlidePlayFragment_BASE_PATH"

        fun create(path: String): Fragment {
            val f = SlidePlayFragment()
            val bundle = Bundle()
            bundle.putString(BASE_PATH, path)
            f.arguments = bundle
            return f
        }

        private fun isVideo(name: String): Boolean {
            return name.toLowerCase().endsWith(".mp4") ||
                    name.toLowerCase().endsWith(".wmv") ||
                    name.toLowerCase().endsWith(".avi") ||
                    name.toLowerCase().endsWith(".rmvb") ||
                    name.toLowerCase().endsWith(".mpeg") ||
                    name.toLowerCase().endsWith(".mpg") ||
                    name.toLowerCase().endsWith(".mp3") ||
                    name.toLowerCase().endsWith(".wav")
        }

        private fun isPicture(name: String): Boolean {
            return name.toLowerCase().endsWith(".jpg") ||
                    name.toLowerCase().endsWith(".bmp") ||
                    name.toLowerCase().endsWith(".png") ||
                    name.toLowerCase().endsWith(".jpeg")
        }
    }
}
