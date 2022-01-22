package com.worter

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.updateLayoutParams
import kotlinx.android.synthetic.main.activity_all_words_list.*

class AllWordsListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_words_list)
        supportActionBar?.hide()

        val fileName = this.intent.extras!!.getString("fileName")!!
        val allWordsList = DBManager.getFile(fileName)!!
        fillList(allWordsList)
        all_words_back_to_menu_button.setOnClickListener { goToMainMenu() }
    }

    private fun fillList(allWordsList : List<RecordModel>) {
        for (record in allWordsList.reversed()) {
            val row = recordToTableRow(record)
            all_words_table.addView(row, 0)
            setWidths(row)
        }
    }

    private fun recordToTableRow(record : RecordModel) : TableRow {
        val tr = TableRow(this)
        val polishText = TextView(this)
        tr.addView(polishText)
        val germanText = TextView(this)
        tr.addView(germanText)

        polishText.text = record.poleng_list[0]
        polishText.textSize = 20f
        polishText.setTypeface(null, Typeface.BOLD)

        germanText.text = record.ger_list[0]
        germanText.textSize = 20f
        return tr
    }

    private fun setWidths(row : TableRow) {
        row.updateLayoutParams {
            width = 1000
        }
        val marginParam = row.layoutParams as ViewGroup.MarginLayoutParams
        marginParam.setMargins(0,0,0,50)
        row.layoutParams = marginParam

        val polishText = row[0] as TextView
        polishText.updateLayoutParams { width = 450 }
        polishText.gravity = Gravity.LEFT

        val germanText = row[1] as TextView
        germanText.updateLayoutParams { width = 450}
        germanText.gravity = Gravity.RIGHT
    }

    private fun goToMainMenu() {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        return
    }
}

