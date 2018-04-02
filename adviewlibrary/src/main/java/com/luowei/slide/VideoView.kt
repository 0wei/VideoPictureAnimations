package com.luowei.slide

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import com.google.android.exoplayer2.*
import com.unistrong.luowei.commlib.Log
import com.unistrong.luowei.kotlin.show
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.unistrong.luowei.kotlin.hide
import java.io.File


/**
 * Created by luowei on 2017/12/5.
 */
class VideoView : TextureView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    //    private var mediaPlayer: MediaPlayer? = null
    private var videoStatus: Int = 0
    private val BASE = 1
//    private val VIDEO_DATA_LOADED = BASE
    private val VIDEO_SURFACE_LOADED = BASE shl 1
    private val VIDEO_ERROR = BASE shl 2
    private val VIDEO_ALLOW_PLAY = BASE shl 3
    enum class ListenState{Start,End}
    var listener: ((state:ListenState) -> Unit?)? = null
    private val DEBUG = false


    // 1. Create a default TrackSelector
    val mainHandler = Handler()
    val bandwidthMeter = DefaultBandwidthMeter()
    val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
    val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
    val mediaPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)


    init {
//        isDrawingCacheEnabled = true
//        mediaPlayer = MediaPlayer()
        surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                mediaPlayer.setVideoSurface(Surface(surface))

//                mediaPlayer?.setSurface(Surface(surface))
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
        mediaPlayer.addListener(object : Player.DefaultEventListener() {
            override fun onPlayerError(error: ExoPlaybackException?) {
                Log.e(error.toString())
                playDone()
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    playDone()
                } else if(playbackState==Player.STATE_READY){
                    listener?.invoke(ListenState.Start)
                }
            }

        })

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        try2PlayVideo()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        releaseMediaPlayer()
    }

    private fun releaseMediaPlayer() {
//        if (mediaPlayer != null) {
//            //// TODO: 2016/10/16 切换界面过慢
//            /*
//            min undequeued buffer count (2) exceeded (dequeued=8 undequeued=1) 日志信息
//             */
//            //            mediaPlayer.setSurface(null);
//            mediaPlayer!!.release()
//            mediaPlayer = null
//        }
        mediaPlayer.release()
    }

    private fun try2PlayVideo(): Boolean {

        if (//videoStatus and VIDEO_DATA_LOADED != 0 &&
                 videoStatus and VIDEO_SURFACE_LOADED != 0
                && videoStatus and VIDEO_ALLOW_PLAY != 0
                && mediaPlayer != null
//                && !mediaPlayer!!.pl
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
            if (DEBUG) Log.d()
//            mediaPlayer!!.start()
            mediaPlayer.playWhenReady = true;
            mediaPlayer.seekTo(0);
//            mediaPlayer!!.playWhenReady = true
            return true
        }
        return false
    }

    var playComplete: Boolean = false
        private set

    fun initVideoResource(videoPath: String) {
        playComplete = false
        videoStatus = videoStatus and VIDEO_ALLOW_PLAY.inv()
        if (DEBUG) Log.d("path=$videoPath")

        val bandwidthMeter = DefaultBandwidthMeter()
// Produces DataSource instances through which media data is loaded.
        val dataSourceFactory = DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "yourApplicationName"), bandwidthMeter)
// This is the MediaSource representing the media to be played.
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.fromFile(File(videoPath)))
// Prepare the player with the source.
        mediaPlayer.prepare(videoSource)
//        mediaPlayer.playWhenReady = true
//        try {
//            mediaPlayer!!.reset()
//            mediaPlayer!!.setDataSource(videoPath)
//            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
//            mediaPlayer!!.prepareAsync()
//            mediaPlayer!!.setOnPreparedListener {
//                videoStatus = videoStatus or VIDEO_DATA_LOADED
//                try2PlayVideo()
//                if (DEBUG) Log.d("on prepared")
//            }
//            mediaPlayer!!.setOnCompletionListener {
//                if (DEBUG) Log.d("completion")
//                playDone()
//            }
//            mediaPlayer!!.setOnErrorListener { mp, what, extra ->
//                playDone()
//                if (DEBUG) Log.e("on error: mp=$mp, what=$what, extra=$extra")
//                true
//            }
//        } catch (e: Exception) {
//            e.printStackTrace();
//            videoStatus = videoStatus or VIDEO_ERROR
//            playDone()
//        }

    }

    private fun playDone() {
        listener?.invoke(ListenState.End)
        playComplete = true
    }

    fun play() {
        show()
        videoStatus = videoStatus or VIDEO_ALLOW_PLAY
        try2PlayVideo()
    }

    fun stop(){
        mediaPlayer.stop()
        hide()
    }


}