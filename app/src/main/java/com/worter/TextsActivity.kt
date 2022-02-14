package com.worter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_texts.*
import android.view.Gravity
import android.widget.PopupWindow
import android.widget.LinearLayout
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat.getSystemService
import android.view.LayoutInflater
import android.view.View.OnTouchListener
import kotlinx.android.synthetic.main.layout_translation_popup.view.*


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
        text_field.movementMethod = LinkMovementMethod.getInstance();
        text_field.text = makeAllWordsClickable("\t\t\t$body")
    }

    private fun concatenateText(lineList: List<String>) : String {
        val avgLineLen = lineList.map { it.length }.average()
        var txt = ""
        for (line in lineList) {
            txt += line
            txt += if (line.length < avgLineLen * 0.7 && ".?!".contains(line.last())) {
                "\n\n\t\t\t "
            } else {
                " "
            }
        }
        return txt.trim()
    }

    private fun makeAllWordsClickable(textBody: String): SpannableString {
        val beginEndIndices = calculateBeginEndWordIndicesOf(textBody)
        val spannable = SpannableString(textBody)

        for ((begin, end) in beginEndIndices) {
            spannable.setSpan(getTranslationClickableSpan(begin, end+1), begin, end+1, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }
        return spannable
    }

    private fun calculateBeginEndWordIndicesOf(str: String): List<Pair<Int, Int>> {
        val idxPairs = mutableListOf<Pair<Int,Int>>()
        val garbageChars = "~`!@#$%^&*()-{}[]<>,.;:\'\"/?\\+=\n\t "
        var previousChar = ' '
        var beginIdx = 0
        for ((idx, c) in str.withIndex()) {
            if(garbageChars.contains(previousChar) and !garbageChars.contains(c)) {
                beginIdx = idx
            }
            if(!garbageChars.contains(previousChar) and garbageChars.contains(c)) {
                idxPairs.add(Pair(beginIdx, idx - 1))
            }
            previousChar = c
        }
        return idxPairs.toList()
    }

    private fun getTranslationClickableSpan(startIdx: Int, endIdx: Int): ClickableSpan {
        val translationClickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textBody: View) {
                val word = (textBody as TextView).text.substring(startIdx, endIdx)
                showTranslationPopup(text_field, TranslationManager.germanToPolish((word)))
            }

            override fun updateDrawState(ds: TextPaint) {
                return
            }
        }
        return translationClickableSpan
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

    @SuppressLint("InflateParams", "SetTextI18n")
    fun showTranslationPopup(view: View, translation: TranslationData) {
        val inflater = this.layoutInflater
        val popupView = inflater.inflate(R.layout.layout_translation_popup, null)

        popupView.translation_popup_word.text = translation.word
        if (translation.meanings.isNotEmpty()) {
            popupView.translation_popup_meanings.text = translation.meanings.joinToString()
        } else {
            popupView.translation_popup_meanings.text = "No translation found"
        }
        for (sentence in translation.sentences.take(3)) {
            val sentenceView = TextView(this)
            sentenceView.text = "- $sentence"
            sentenceView.setTextColor(ContextCompat.getColor(this, R.color.dirty_white))
            sentenceView.textSize = 18F
            popupView.translation_popup_sentences.addView(sentenceView)
        }
        popupView.translation_popup_add_word_button.setOnClickListener {
            DBManager.addWord(translation)
        }

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        popupWindow.elevation = 20F
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.BLUE));
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        popupView.setOnTouchListener { v, _ ->
            popupWindow.dismiss()
            v.performClick()
        }
    }

}
