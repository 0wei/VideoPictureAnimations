package com.luowei.slide


import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup

import com.unistrong.luowei.adsslidelib.R
import com.unistrong.luowei.commlib.Log


/**
 * 视频播放
 */
class VideoFragment : Fragment(), ISlide.SlideItem {

    private var mediaPlayer: MediaPlayer? = null
    private var videoStatus: Int = 0
    private var position: Int = 0
    private lateinit var textureView: TextureView

    private var slide: ISlide? = null
    var videoPath: String? = null
        private set

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.slide_fragment_slide_video, container, false)
        textureView = view.findViewById(R.id.ads_slide_video_textureView)
        mediaPlayer = MediaPlayer()

        val arguments = arguments
        if (arguments != null) {
            videoPath = arguments.getString(PATH)
        }

        //        Glide.with(this).load(videoPath).into(prevImageView);
        //        prevImageView.setImageBitmap(getVideoThumbnail(videoPath));
        initMediaPlayerSurface()
        initVideoResource()
        //        if(DEBUG)Logger.getLogger().d("onCreateView" + position);
        return view
    }

    fun getBitmap(): Bitmap? {
        return textureView.bitmap
    }

    fun getVideoThumbnail(filePath: String): Bitmap? {

        var bitmap: Bitmap? = null
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(filePath)
            bitmap = retriever.frameAtTime
        } catch (e: RuntimeException) {
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }

        }
        return bitmap
    }

    private fun initMediaPlayerSurface() {
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                mediaPlayer!!.setSurface(Surface(surface))
                //                mediaPlayer.setDisplay(holder);
                videoStatus = videoStatus or VIDEO_SURFACE_LOADED
                try2PlayVideo()

            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                videoStatus = videoStatus and VIDEO_SURFACE_LOADED.inv()
                releaseMediaPlayer()
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

            }
        }
        //        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
        //            @Override
        //            public void surfaceCreated(SurfaceHolder holder) {
        //                if (DEBUG) Logger.getLogger().d("surfaceCreated");
        //
        //                mediaPlayer.setDisplay(holder);
        //                videoStatus |= VIDEO_SURFACE_LOADED;
        //                try2PlayVideo();
        //
        //            }
        //
        //            @Override
        //            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //
        //            }
        //
        //            @Override
        //            public void surfaceDestroyed(SurfaceHolder holder) {
        //                videoStatus &= ~VIDEO_SURFACE_LOADED;
        //
        //            }
        //        });
    }


    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

        pauseVideo()
    }

    private fun errorCheck() {
        if (videoStatus and VIDEO_ERROR != 0) {
            //            videoStatus &= ~VIDEO_ERROR;
            //此处直接调用slideNext();导致异常
            //            if(isResumed()){
            //                slideNext();
            //            }
            Handler().postDelayed({
                //                    if (!slideNext()) { //请求滑动到下一页失败,则尝试再次播放视频
                //
                //                        try2PlayVideo();
                //                    }
                slideNext()
            }, 1000)
        }
    }

    private fun initVideoResource() {
        try {
            Log.d("load video $videoPath, ${this}")
            mediaPlayer!!.setDataSource(videoPath)
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer!!.prepareAsync()
            mediaPlayer!!.setOnPreparedListener {
                videoStatus = videoStatus or VIDEO_DATA_LOADED
                Log.d("on prepared")
                try2PlayVideo()

            }
            mediaPlayer!!.setOnCompletionListener {
                Log.d("completion")
                if (!slideNext()) { //请求滑动到下一页失败,则尝试再次播放视频
                    try2PlayVideo()
                } else{
                    Log.d("slide to next ok")
                }
            }
            mediaPlayer!!.setOnErrorListener { mp, what, extra ->
                Log.e("on error: mp=$mp, what=$what, extra=$extra")
                if (!slideNext()) { //请求滑动到下一页失败,则尝试再次播放视频
                    try2PlayVideo()
                }

                true
            }


        } catch (e: Exception) {
                        e.printStackTrace();
            videoStatus = videoStatus or VIDEO_ERROR
            //            new Handler().post(new Runnable() {
            //                @Override
            //                public void run() {
            ////                    if (!slideNext()) { //请求滑动到下一页失败,则尝试再次播放视频
            ////                        try2PlayVideo();
            ////                    }
            //                    slideNext();
            //
            //                }
            //            });
        }

    }

    private fun slideNext(): Boolean {
        return slide!!.requestSlideNext()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!isVisibleToUser) {
            pauseVideo()
        } else {
            try2PlayVideo()
        }
    }

    private fun try2PlayVideo(): Boolean {
        if (userVisibleHint) {
            errorCheck()
        }

        if (videoStatus and VIDEO_DATA_LOADED != 0
                && videoStatus and VIDEO_SURFACE_LOADED != 0
                && mediaPlayer != null
                && !mediaPlayer!!.isPlaying
                && userVisibleHint) {
            //            prevImageView.setVisibility(View.GONE);
            mediaPlayer!!.start()
            return true
        }
        return false
    }


    private fun pauseVideo() {
        if (/*(videoStatus & VIDEO_DATA_LOADED) != 0&& (videoStatus & VIDEO_SURFACE_LOADED) != 0&& */
        mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        releaseMediaPlayer()

    }

    private fun releaseMediaPlayer() {
        if (mediaPlayer != null) {
            //// TODO: 2016/10/16 切换界面过慢
            /*
            min undequeued buffer count (2) exceeded (dequeued=8 undequeued=1) 日志信息
             */
            //            mediaPlayer.setSurface(null);
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    fun setItem(position: Int) {
        this.position = position
    }


    override fun canSlide(): Boolean {
        //        Logger.getLogger().d("videoStatus=%s", Integer.toBinaryString(videoStatus));
        return false
    }

    override fun setSlide(slide: ISlide) {
        this.slide = slide
    }

    companion object {
        private val DEBUG = false

        private val PATH = "PATH"
        private val BASE = 1
        private val VIDEO_DATA_LOADED = BASE
        private val VIDEO_SURFACE_LOADED = BASE shl 1
        private val VIDEO_ERROR = BASE shl 2

        fun create(path: String): VideoFragment {
            val videoFragment = VideoFragment()
            val args = Bundle()
            args.putString(PATH, path)
            videoFragment.arguments = args
            return videoFragment
        }
    }

}// Required empty public constructor
//        audioManager = (AudioManager) App.getContext().getSystemService(Context.AUDIO_SERVICE);
