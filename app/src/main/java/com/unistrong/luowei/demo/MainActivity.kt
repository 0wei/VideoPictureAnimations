package com.unistrong.luowei.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.luowei.slide.Advertisement

class MainActivity : AppCompatActivity() {

    private lateinit var advertisement: Advertisement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        advertisement = findViewById(R.id.slide_advertisement) as Advertisement
        advertisement.setFragmentManager(supportFragmentManager)

    }
}
