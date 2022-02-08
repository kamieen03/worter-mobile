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
import kotlinx.android.synthetic.main.activity_texts_menu.*

class TextsMenuActivity : AppCompatActivity() {
private var selectedText = "10"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_texts_menu)
        supportActionBar?.hide()
        button_select_text.setOnClickListener { startTextsActivity() }
        fillTextsList()
    }

    private fun fillTextsList() {
        for (fName in DBManager.getTextsFileNames()) {
            val fb = getTextsListButton(fName)
            texts_list.addView(fb)
            setFbMargin(fb)
        }
        (texts_list[0] as Button).callOnClick()
    }

    private fun getTextsListButton(name: String): Button {
        val fb = Button(this)
        fb.text = name
        fb.textSize = 20f
        fb.setTextColor(ContextCompat.getColor(this, R.color.font))
        fb.setOnClickListener { textsTableButtonOnClickListener(fb)}
        return fb
    }

    private fun setFbMargin(fb: Button) {
        val marginParam = fb.layoutParams as ViewGroup.MarginLayoutParams
        marginParam.setMargins(0,20,0,20)
        fb.layoutParams = marginParam
    }

    private fun textsTableButtonOnClickListener(fb: Button) {
        texts_list.children.forEach { it as Button
            it.background = getTextButtonShape(DBManager.isTextRead(it.text.toString()))
        }
        fb.background.setTint(ContextCompat.getColor(this, R.color.Bronze))
        selectedText = fb.text.toString()
    }

    private fun getTextButtonShape(isTextRead: Boolean): PaintDrawable {
        val color = if (isTextRead) R.color.learned else R.color.not_yet_learned
        val pd = PaintDrawable()
        pd.shape = RectShape()
        pd.setCornerRadius(25f)
        pd.setTint(ContextCompat.getColor(this, color))
        return pd
    }

    private fun startTextsActivity() {
        val intent = Intent(this, TextsActivity::class.java)
        intent.putExtra("textName", selectedText)
        startActivity(intent)
    }
}
