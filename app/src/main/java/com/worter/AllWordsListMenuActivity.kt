package com.worter

import android.content.Intent
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_all_words_list_menu.*
import kotlinx.android.synthetic.main.activity_all_words_list_menu.file_list
import kotlinx.android.synthetic.main.activity_review_menu.*

class AllWordsListMenuActivity : AppCompatActivity() {
    private var selectedFile = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_words_list_menu)
        supportActionBar?.hide()
        button_select_word_list.setOnClickListener { startAllWordsListActivity() }
        fillFileList()
    }

    private fun fillFileList() {
        for (fName in DBManager.getFileNames()) {
            val fb = getFileListButton(fName)
            file_list.addView(fb)
            setFbMargin(fb)
        }
        (file_list[0] as Button).callOnClick()
    }

    private fun getFileListButton(name: String): Button {
        val fb = Button(this)
        fb.text = name
        fb.textSize = 20f
        fb.setTextColor(ContextCompat.getColor(this, R.color.font))
        fb.setOnClickListener { fileTableButtonOnClickListener(fb)}
        return fb
    }

    private fun setFbMargin(fb: Button) {
        val marginParam = fb.layoutParams as ViewGroup.MarginLayoutParams
        marginParam.setMargins(0,20,0,20)
        fb.layoutParams = marginParam
    }

    private fun fileTableButtonOnClickListener(fb: Button) {
        file_list.children.forEach { it as Button
            it.background = getFileButtonShape()
        }
        fb.background.setTint(ContextCompat.getColor(this, R.color.Bronze))
        selectedFile = fb.text.toString()
    }

    private fun getFileButtonShape(): PaintDrawable {
        val pd = PaintDrawable()
        pd.shape = RectShape()
        pd.setCornerRadius(25f)
        pd.setTint(ContextCompat.getColor(this, R.color.learned))
        return pd
    }

    private fun startAllWordsListActivity() {
        val intent = Intent(this, AllWordsListActivity::class.java)
        intent.putExtra("fileName", selectedFile)
        startActivity(intent)
    }
}
