package com.worter

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_texts.*

class TextsActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_texts)
        supportActionBar?.hide()

        val textName = this.intent.extras!!.getString("textName")!!
        prepareText(textName)
        updateButton(textName)
        mark_text_as_read_button.setOnClickListener {
            DBManager.flipTextRead(textName)
            updateButton(textName)
        }
    }

    override fun onPause() {
        super.onPause()
        DBManager.saveReadTexts()
    }

    @SuppressLint("SetTextI18n")
    private fun prepareText(textName: String) {
        val txt = DBManager.getText(textName)
        val lineList = txt.split("\n")
        val title = lineList[0]
        val body = concatenateText(lineList.drop(1))
        text_title.text = title
        text_title.setTypeface(null, Typeface.BOLD)
        text_field.text = "\t\t\t$body"
    }

    private fun concatenateText(lineList: List<String>) : String {
        val avgLineLen = lineList.map { it.length }.average()
        var txt = ""
        for (line in lineList) {
            txt += line
            txt += if (line.length < avgLineLen * 0.7 && ".?!".contains(line.last())) {
                "\n\n\t\t\t"
            } else {
                " "
            }
        }
        return txt.trim()
    }

    private fun updateButton(textName: String) {
        if (DBManager.isTextRead(textName)) {
            mark_text_as_read_button.text = getString(R.string.text_unread)
            mark_text_as_read_button.background.setTint(ContextCompat.getColor(this, R.color.not_yet_learned))
        } else {
            mark_text_as_read_button.text = getString(R.string.mark_as_read)
            mark_text_as_read_button.background.setTint(ContextCompat.getColor(this, R.color.learned))
        }
    }
}