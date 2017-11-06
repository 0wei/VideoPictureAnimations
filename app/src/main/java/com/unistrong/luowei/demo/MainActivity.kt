package com.unistrong.luowei.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.SeekBar
import com.luowei.slide.SlideAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

//    private lateinit var advertisement: Advertisement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (findViewById(R.id.roteSeekBar) as SeekBar).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                advertisement.setProgress(progress)
//                slide_advertisement.nextBitmap = BitmapFactory.decodeFile("/sdcard/adv2.png")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        advertisement.setFragmentManager(supportFragmentManager)
        advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Image, "/sdcard/adv1.png"))
        advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Image, "/sdcard/adv2.png"))
        advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Image, "/sdcard/adv3.png"))
        advertisement.addItem(SlideAdapter.Item(SlideAdapter.ItemType.Video, "/sdcard/1.mp4"))
//        slide_advertisement.currentBitmap = BitmapFactory.decodeFile("/sdcard/adv1.png")

    }
}
