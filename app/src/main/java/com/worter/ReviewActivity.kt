package com.worter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_review.*
import kotlin.random.Random

class ReviewActivity : AppCompatActivity() {
    private lateinit var worterList: List<RecordModel>
    private lateinit var mode: String
    private var worterListIdx: Int = 0
    private var germanTextVisible = false
    private var failedWords = mutableListOf<RecordModel>()
    private var wortHardButtonBlocked = false
    private var wortEasyButtonBlocked = false
    private lateinit var fileName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        supportActionBar?.hide()

        val b = this.intent.extras
        mode = b!!.getString("mode")!!
        fileName = b.getString("fileName")!!
        worterList = DBManager.getFile(fileName)!!.toMutableList().shuffled()
        setOnClickListeners()
        initIdx()
        showNextRecord()
    }

    override fun onPause() {
        super.onPause()
        DBManager.saveDb()
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
            if (germanTextVisible and !wortEasyButtonBlocked) {
                wortEasyButtonBlocked = true
                if (worterList[worterListIdx].hardness > 1) {
                    worterList[worterListIdx].hardness--
                }
                showNextRecord()
            }
        }
        wort_hard_button.setOnClickListener {
            if (germanTextVisible and !wortHardButtonBlocked) {
                wortHardButtonBlocked = true
                worterList[worterListIdx].hardness++
                failedWords.add(worterList[worterListIdx])
                showNextRecord()
            }
        }
    }

    private fun initIdx() {
        worterListIdx = if (mode == "CONSECUTIVE") {
            -1
        } else {
            worterList.indices.random()
        }
        if (mode == "RANDOM") {
            worter_idx.visibility = View.GONE
        }
    }

    private fun updateWorterListIdx() {
        if (mode == "CONSECUTIVE") {
            worterListIdx++
        } else {
            worterListIdx = getRandomRecordIdx()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTexts() {
        worter_polish_text.text = worterList[worterListIdx].poleng_list.reduce{ a, b -> "$a\n$b"}
        worter_german_text.text = worterList[worterListIdx].ger_list[0]
        if (worterList[worterListIdx].ger_list.size > 1) {
            worter_german_sentences.text =
                worterList[worterListIdx].ger_list.drop(1).map { "- $it" }.reduce { a, b -> "$a\n$b" }
        } else {
            worter_german_sentences.text = ""
        }
        worter_idx.text = "${worterListIdx+1}/${worterList.size}"
        worter_german_text.setTextColor(ContextCompat.getColor(this, R.color.background))
        worter_german_sentences.setTextColor(ContextCompat.getColor(this, R.color.background))
    }

    private fun showNextRecord() {
        updateWorterListIdx()
        if (worterListIdx == worterList.size) {
            showFailedWords()
        } else {
            updateTexts()
            germanTextVisible = false
            wortHardButtonBlocked = false
            wortEasyButtonBlocked = false
        }
    }

    private fun getRandomRecordIdx() : Int {
        val hardness2: List<Int> = worterList.map { it.hardness * it.hardness }
        val weights: List<Float> = hardness2.map { it.toFloat() / hardness2.sum() }
        var s = weights[0]
        var idx = 0
        val r = Random.nextFloat()
        while (idx < weights.size) {
            if (r < s) {
                break
            } else {
                idx++
                s += weights[idx]
            }
        }
        return idx
    }

    private fun showFailedWords() {
        val intent = Intent(this, WordListActivity::class.java)
        intent.putExtra("failedWordsList", failedWords as ArrayList<RecordModel>)
        intent.putExtra("fileName", fileName)
        startActivity(intent)
    }
}

