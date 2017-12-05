package com.luowei.slide

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import com.unistrong.luowei.commlib.Log
import com.unistrong.luowei.kotlin.show

/**
 * Created by luowei on 2017/12/5.
 */
class VideoView : TextureView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private var mediaPlayer: MediaPlayer? = null
    private var videoStatus: Int = 0
    private val BASE = 1
    private val VIDEO_DATA_LOADED = BASE
    private val VIDEO_SURFACE_LOADED = BASE shl 1
    private val VIDEO_ERROR = BASE shl 2
    private val VIDEO_ALLOW_PLAY = BASE shl 3
    var listener: (() -> Unit?)? = null
    private val DEBUG = false

    init {
//        isDrawingCacheEnabled = true
        mediaPlayer = MediaPlayer()
        surfaceTextureListener = object : TextureView.SurfaceTextureListener {
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
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        try2PlayVideo()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        releaseMediaPlayer();
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

    private fun try2PlayVideo(): Boolean {

        if (videoStatus and VIDEO_DATA_LOADED != 0
                && videoStatus and VIDEO_SURFACE_LOADED != 0
                && videoStatus and VIDEO_ALLOW_PLAY != 0
                && mediaPlayer != null
                && !mediaPlayer!!.isPlaying
                ) {
//            val alpha = imageView.animate().alpha(0f)
//            alpha.setListener(object : AnimatorListenerAdapter() {
//                override fun onAnimationEnd(animation: Animator?) {
//                    super.onAnimationEnd(animation)
//                    imageView.visibility = View.INVISIBLE
//                    imageView.alpha = 1f
//                }
//            })
//            alpha.start()
            Log.d()
            mediaPlayer!!.start()
            return true
        }
        return false
    }

    var playComplete: Boolean = false
        private set

    fun initVideoResource(videoPath: String) {
        playComplete = false
        videoStatus = videoStatus and VIDEO_ALLOW_PLAY.inv()
        try {
            Log.d("path=$videoPath")
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(videoPath)
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer!!.prepareAsync()
            mediaPlayer!!.setOnPreparedListener {
                videoStatus = videoStatus or VIDEO_DATA_LOADED
                try2PlayVideo()
                if (DEBUG) Log.d("on prepared")
            }
            mediaPlayer!!.setOnCompletionListener {
                //                if (!slideNext()) { //请求滑动到下一页失败,则尝试再次播放视频
//                    try2PlayVideo()
//                } else {
//                    if (DEBUG) Log.d("viewPager to next ok")
//                }
                if (DEBUG) Log.d("completion")
                playDone()

            }
            mediaPlayer!!.setOnErrorListener { mp, what, extra ->
                playDone()
                if (DEBUG) Log.e("on error: mp=$mp, what=$what, extra=$extra")
                true
            }
        } catch (e: Exception) {
            e.printStackTrace();
            videoStatus = videoStatus or VIDEO_ERROR
            playDone()
        }

    }

    private fun playDone() {
        listener?.invoke()
        playComplete = true
    }

    fun play() {
        show()
        videoStatus = videoStatus or VIDEO_ALLOW_PLAY
        try2PlayVideo()

    }

}