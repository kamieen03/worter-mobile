package com.worter

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.core.view.children
import com.worter.databinding.ActivityFullscreenBinding
import kotlinx.android.synthetic.main.activity_fullscreen.file_list

class FullscreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenBinding
    private val fileNames = Array(25) {i -> "file$i.json"}

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        fillFileList()
    }

    private fun fillFileList() {
        for (fName in fileNames) {
            val tv = TextView(this)
            tv.text = fName
            tv.gravity = Gravity.CENTER
            tv.textSize = 30F
            file_list.addView(tv)
        }

        file_list.children.forEach {
            it.setOnClickListener { it1 -> singleFileOnClickListener(it1 as TextView) }
        }
    }

    private fun singleFileOnClickListener(tv: TextView) {
        file_list.children.forEach { it as TextView
            it.setTextColor(Color.BLACK)
        }
        tv.setTextColor(Color.YELLOW)
    }
}