package com.worter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_review.*

class ReviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        supportActionBar?.hide()

        val b = this.intent.extras
        val mode = b!!.getString("mode")
        val fileName = b.getString("fileName")
    }
}

