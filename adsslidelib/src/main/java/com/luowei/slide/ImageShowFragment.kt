package com.luowei.slide


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.unistrong.luowei.adsslidelib.R
import com.unistrong.luowei.commlib.Log

class ImageShowFragment : Fragment(), ISlide.SlideItem {


    private var position: Int = 0

    var imagePath: String? = null

    private lateinit var imageView: Roll3DContainer
    private lateinit var images: Array<out String>


    private var imageIndex = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.slide_fragment_image_show, container, false)
        imageView = view.findViewById<Roll3DContainer>(R.id.ads_image_imageView_slide)
        imageView.listener = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (DEBUG) Log.d("imageIndex=${imageIndex - 1}, ${this@ImageShowFragment}")
                Handler().post { viewPager?.requestSlideNext(true, false) }
            }
        }
        imagePath = arguments.getString(PATH)
        images = arguments.getStringArray(IMAGES_ARRAY)
        return view
    }

    override fun toString(): String {
        return "${super.toString()}, currentItem=${viewPager?.currentItem}"
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView.currentBitmap = BitmapFactory.decodeFile(images[imageIndex%images.size])
    }

    override fun canSlide(): Boolean {
        if (DEBUG) Log.d("imageIndex=$imageIndex, $this")
        imageIndex++
        if (imageIndex >= images.size) {
            return true
        }
        imageView.nextBitmap = BitmapFactory.decodeFile(images[imageIndex])
        return false
    }

    private var viewPager: SlideViewPager? = null

    override fun setSlide(slide: ISlide) {
        viewPager = slide as SlideViewPager
        if (DEBUG) Log.d("currentItem=${viewPager!!.currentItem}, $this")
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {

        private val PATH = "PATH"
        private val DEBUG = false
        private val IMAGES_ARRAY = "IMAGES_ARRAY"

        fun create(path: String? = null, images: Array<String>): ImageShowFragment {
            val imageShowFragment = ImageShowFragment()
            val args = Bundle()
            args.putStringArray(IMAGES_ARRAY, images)
            args.putString(PATH, path)
            imageShowFragment.arguments = args
            return imageShowFragment
        }
    }

    fun getBitmap(): Bitmap? {
        return imageView.currentBitmap
    }
}// Required empty public constructor
