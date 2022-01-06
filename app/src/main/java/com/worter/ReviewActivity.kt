package com.worter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ReviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        val b = this.intent.extras
        val mode = b!!.getString("mode")
    }
}
