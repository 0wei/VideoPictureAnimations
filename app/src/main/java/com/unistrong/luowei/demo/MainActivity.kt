package com.unistrong.luowei.demo

import android.Manifest
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.SeekBar
import com.luowei.slide.SlideAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

//    private lateinit var advertisement: Advertisement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        (findViewById<SeekBar>(R.id.roteSeekBar)).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                advertisement.setProgress(progress)
//                slide_advertisement.nextBitmap = BitmapFactory.decodeFile("/sdcard/adv2.png")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        var count = 0
        add.setOnClickListener {
            advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Image, "/sdcard/abc/${count++ % 4}.jpg"))
        }
        addvideo.setOnClickListener {
            advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Video, "/sdcard/1.mp4"))
        }

        rm.setOnClickListener {
            advertisement.playlist.apply { removeAt(size - 1) }
            advertisement.notifyDataChange()
        }
        advertisement.setDefaultImageFile("/sdcard/abc/0.jpg")

        next.setOnClickListener { advertisement.next() }
        prev.setOnClickListener { advertisement.prev() }
//        advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Image, "/sdcard/abc/1.jpg"))
//        advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Image, "/sdcard/ad/adv3.png"))
//        advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Image, "/sdcard/2h/1.jpg"))
//        advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Image, "/sdcard/2h/2.jpg"))
//        advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Video, "/sdcard/1.mp4"))
//        advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Image, "/sdcard/2h/3.jpg"))
//        advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Video, "/sdcard/2.mp4"))

//        advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Video, "/sdcard/2.mp4"))
    }
}
