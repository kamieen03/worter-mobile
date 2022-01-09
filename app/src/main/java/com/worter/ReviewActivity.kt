package com.worter

import android.annotation.SuppressLint
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
        println(fileName)
        worterList = DBManager.db[fileName]!!
        setOnClickListeners()
        initIdx()
        showNextRecord()
    }

    private fun setOnClickListeners() {
        worter_german_text.setOnClickListener {
            worter_german_text.setTextColor(worter_polish_text.currentTextColor)
            worter_german_sentences.setTextColor(worter_idx.currentTextColor)
            germanTextVisible = true
        }
        worter_german_sentences.setOnClickListener {
            worter_german_text.setTextColor(worter_polish_text.currentTextColor)
            worter_german_sentences.setTextColor(worter_idx.currentTextColor)
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
        if (mode == "RANDOM") {
            worter_idx.visibility = View.GONE
        }
    }

    //TODO: add persistent db updating
    @SuppressLint("SetTextI18n")
    private fun showNextRecord() {
        worter_polish_text.text = worterList[idx].poleng_list.reduce{a, b -> "$a\n$b"}
        worter_german_text.text = worterList[idx].ger_list[0]
        if (worterList[idx].ger_list.size > 1) {
            worter_german_sentences.text =
                worterList[idx].ger_list.drop(1).map { "- $it" }.reduce { a, b -> "$a\n$b" }
        }
        worter_idx.text = "${idx+1}/${worterList.size}"

        worter_german_text.setTextColor(ContextCompat.getColor(this, R.color.background))
        worter_german_sentences.setTextColor(ContextCompat.getColor(this, R.color.background))
        germanTextVisible = false

        if (mode == "CONSECUTIVE") {
            idx++ //TODO: show summary when idx goes out of range
        } else {
            idx = worterList.indices.random()
        }
    }
}

