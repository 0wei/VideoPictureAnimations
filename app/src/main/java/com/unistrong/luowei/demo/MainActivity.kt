package com.unistrong.luowei.demo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

//    private lateinit var advertisement: Advertisement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

        /* advertisement.setFragmentManager(supportFragmentManager)
         advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Image, "/sdcard/adv1.png"))
         advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Image, "/sdcard/adv2.png"))
         advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Image, "/sdcard/adv3.png"))
         advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Video, "/sdcard/1.mp4"))*/
//        advertisement.currentBitmap = BitmapFactory.decodeResource(resources, R.mipmap.maxresdefault)
//        advertisement.nextBitmap=  BitmapFactory.decodeResource(resources, R.mipmap.fdae)
        advertisement.ImageViewA.currentBitmap = BitmapFactory.decodeResource(resources, R.mipmap.fdae)
//        advertisement.ImageViewA.nextBitmap = BitmapFactory.decodeResource(resources, R.mipmap.maxresdefault)
        advertisement.ImageViewB.currentBitmap = BitmapFactory.decodeResource(resources, R.mipmap.maxresdefault)
//        advertisement.ImageViewB.nextBitmap = BitmapFactory.decodeResource(resources, R.mipmap.fdae)
        advertisement.ImageViewC.currentBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ddff)
//        advertisement.ImageViewC.nextBitmap = BitmapFactory.decodeResource(resources, R.mipmap.maxresdefault)
//        advertisement.currentBitmap = BitmapFactory.decodeFile("/sdcard/adv1.png")
        var count = 0
        button.setOnClickListener {
            com.unistrong.luowei.commlib.Log.d()
            if (count++ % 2 == 0)
                advertisement.recycleView.get(1).nextBitmap = BitmapFactory.decodeResource(resources, R.mipmap.maxresdefault)
            else advertisement.recycleView.get(1).nextBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ddff)

        }

    }
}
