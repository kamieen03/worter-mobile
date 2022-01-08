package com.worter

import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_review.*

class ReviewActivity : AppCompatActivity() {
    private lateinit var worterList: List<RecordModel>
    private lateinit var mode: String
    private var idx: Int = 0
    private var germanTextVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        supportActionBar?.hide()

        val b = this.intent.extras
        mode = b!!.getString("mode")!!
        val fileName = b.getString("fileName")
        worterList = DBManager(this).decodeJsonFile(fileName!!)
        setOnClickListeners()
        initIdx()
        showNextRecord()
    }

    private fun setOnClickListeners() {
        worter_german_text.setOnClickListener {
            worter_german_text.setTextColor(worter_polish_text.currentTextColor)
            germanTextVisible = true
        }

        wort_easy_button.setOnClickListener {
            if (germanTextVisible) {
                if (worterList[idx].hardness > 1) {
                    worterList[idx].hardness--
                }
                showNextRecord()
            }
        }

        wort_hard_button.setOnClickListener {
            if (germanTextVisible) {
                worterList[idx].hardness++
                showNextRecord()
            }
        }
    }

    private fun initIdx() {
        idx = if (mode == "CONSECUTIVE") {
            0
        } else {
            worterList.indices.random()
        }
    }

    //TODO: add persistent db updating
    private fun showNextRecord() {
        worter_polish_text.text = worterList[idx].poleng_list.map{"- $it"}.reduce{a, b -> "$a\n$b"}
        worter_german_text.text = worterList[idx].ger_list.map{"- $it"}.reduce{a, b -> "$a\n$b"}
        worter_german_text.setTextColor(ContextCompat.getColor(this, R.color.cafe_au_lait))
        germanTextVisible = false
        if (mode == "CONSECUTIVE") {
            idx++ //TODO: show summary when idx goes out of range
        } else {
            idx = worterList.indices.random()
        }
    }
}

