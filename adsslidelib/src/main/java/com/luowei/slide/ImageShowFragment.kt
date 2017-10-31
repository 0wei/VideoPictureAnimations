package com.luowei.slide


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.unistrong.luowei.adsslidelib.R
import com.unistrong.luowei.commlib.Log

class ImageShowFragment : Fragment(), ISlide.SlideItem {


    private var position: Int = 0
    private val DEBUG=false
    var imagePath: String? = null
    private lateinit var imageView: ImageView
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.slide_fragment_image_show, container, false)
        imageView = view.findViewById(R.id.ads_image_imageView_slide)
        imagePath = arguments.getString(PATH)
        if(DEBUG)Log.d("$imagePath")
        Glide.with(this).load(imagePath).into(imageView)
        return view
    }


    fun setItem(position: Int) {
        this.position = position
    }


    override fun canSlide(): Boolean {
        return true
    }

    override fun setSlide(slide: ISlide) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {


        private val PATH = "PATH"

        fun create(path: String): ImageShowFragment {
            val imageShowFragment = ImageShowFragment()
            val args = Bundle()
            args.putString(PATH, path)
            imageShowFragment.arguments = args
            return imageShowFragment
        }
    }
}// Required empty public constructor
