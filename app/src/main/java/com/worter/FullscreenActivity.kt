package com.worter

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TableRow
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.get
import com.worter.databinding.ActivityFullscreenBinding
import kotlinx.android.synthetic.main.activity_fullscreen.*

class FullscreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenBinding
    private val fileNumbers = Array(50) {i -> "$i"}

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        fillFileTable()
    }

    private fun fillFileTable() {
        var col = 0
        val maxCol = 2
        var tr = TableRow(this)
        val firstRow = tr
        for (fName in fileNumbers) {
            tr.addView(getFileTableButton(fName))
            if (col == maxCol) {
                file_table.addView(tr)
                tr = TableRow(this)
            }
            col = (col+1) % (maxCol + 1)
        }
        if (tr.childCount > 0) {
            file_table.addView(tr)
        }
        (firstRow[0] as Button).callOnClick()
    }

    private fun getFileTableButton(name: String): Button {
        val fb = Button(this)
        fb.text = name
        fb.setOnClickListener { fileTableButtonOnClickListener(fb)}
        return fb
    }

    private fun fileTableButtonOnClickListener(fb: Button) {
        file_table.children.forEach { it as TableRow
            it.children.forEach { it1 -> it1 as Button
                it1.background.setTint(Color.LTGRAY)
            }
        }
        fb.background.setTint(ContextCompat.getColor(this, R.color.Bronze))
    }
}