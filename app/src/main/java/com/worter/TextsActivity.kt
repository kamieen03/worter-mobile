package com.worter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
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
        text_title.setTextColor(Color.DKGRAY)
        text_title.setTypeface(null, Typeface.BOLD)
        text_field.movementMethod = LinkMovementMethod.getInstance();
        text_field.text = makeAllWordsClickable("\t\t\t$body")
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

    private fun makeAllWordsClickable(textBody: String): SpannableString {
        val spaceIndices = indicesOf(textBody, ' ')
        val spannable = SpannableString(textBody)

        spannable.setSpan(getTranslationClickableSpan(3, spaceIndices[0]),
            3, spaceIndices[0], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        for (i in 1 until spaceIndices.size) {
            if (spaceIndices[i] - spaceIndices[i-1] > 2) {
                val startIdx = spaceIndices[i - 1] + 1
                val endIdx = spaceIndices[i]
                spannable.setSpan(getTranslationClickableSpan(startIdx, endIdx),
                    startIdx, endIdx, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        spannable.setSpan(getTranslationClickableSpan(spaceIndices.last() + 1, spannable.length),
            spaceIndices.last() + 1, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
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

fun indicesOf(str: String, char: Char): List<Int> {
    val indices = mutableListOf<Int>()
    for ((idx, c) in str.withIndex()) {
        if (c == char) {
            indices.add(idx)
        }
    }
    return indices.toList()
}

fun getTranslationClickableSpan(startIdx: Int, endIdx: Int): ClickableSpan {
    val translationClickableSpan: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textBody: View) {
            val word = (textBody as TextView).text.substring(startIdx, endIdx)
            println(TranslationManager.germanToPolish((word)))
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = Color.DKGRAY
            ds.isUnderlineText = false
        }
    }
    return translationClickableSpan
}
