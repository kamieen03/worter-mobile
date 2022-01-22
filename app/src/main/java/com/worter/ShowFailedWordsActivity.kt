package com.worter

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.updateLayoutParams
import kotlinx.android.synthetic.main.activity_show_failed_words.*

class ShowFailedWordsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_failed_words)
        supportActionBar?.hide()

        val failedWordsList = intent.getSerializableExtra("failedWordsList") as ArrayList<RecordModel>
        fillList(failedWordsList)
        failed_words_back_to_menu_button.setOnClickListener { goToMainMenu() }
    }

    private fun fillList(failedWordsList : List<RecordModel>) {
        for (record in failedWordsList.reversed()) {
            val row = recordToTableRow(record)
            failed_words_table.addView(row, 0)
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

