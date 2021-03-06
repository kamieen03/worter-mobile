package com.worter

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.updateLayoutParams
import com.google.android.material.color.MaterialColors
import kotlinx.android.synthetic.main.activity_word_list.*

class WordListActivity : AppCompatActivity() {
    private lateinit var defaultTextColor: ColorStateList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_list)

        val fileName = this.intent.extras!!.getString("fileName")!!
        var wordList = DBManager.getFile(fileName)!!
        val showList = if (intent.getSerializableExtra("failedWordsList") != null) {
            intent.getSerializableExtra("failedWordsList") as ArrayList<RecordModel>
        } else {
            wordList
        }
        defaultTextColor = TextView(this).textColors
        fillList(wordList, showList)
        word_list_back_to_menu_button.setOnClickListener { goToMainMenu() }
    }

    override fun onPause() {
        super.onPause()
        DBManager.saveDb()
    }

    private fun fillList(allWordsList : List<RecordModel>, showList: List<RecordModel>) {
        for (record in allWordsList.reversed()) {
            var show = false
            for (tempRecord in showList) {
                if (tempRecord.poleng_list.contentEquals(record.poleng_list)) show = true
            }
            if (!show) continue

            val row = recordToTableRow(record)
            word_list_table.addView(row, 0)
            colorRow(row, record.show_as_red_in_word_list)
            setWidths(row)
            row.setOnClickListener { switchRecordColor(row, record) }
        }
    }

    private fun recordToTableRow(record : RecordModel) : TableRow {
        val tr = TableRow(this)
        val polishText = TextView(this)
        tr.addView(polishText)
        val germanText = TextView(this)
        tr.addView(germanText)

        polishText.text = if (record.poleng_list.size > 1){
            record.poleng_list.drop(1).joinToString()
        } else {
            record.poleng_list[0]
        }
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
        polishText.updateLayoutParams { width = 480 }
        polishText.gravity = Gravity.LEFT

        val germanText = row[1] as TextView
        germanText.updateLayoutParams { width = 480}
        germanText.gravity = Gravity.RIGHT
    }

    private fun colorRow(row: TableRow, shouldColorRed: Boolean) {
        val color = if (shouldColorRed) {
            ContextCompat.getColor(this, R.color.cinamon_satin)
        } else {
            defaultTextColor.defaultColor
        }
        (row[0] as TextView).setTextColor(color)
        (row[1] as TextView).setTextColor(color)
    }

    private fun switchRecordColor(row: TableRow, record: RecordModel) {
        record.show_as_red_in_word_list = !record.show_as_red_in_word_list
        colorRow(row, record.show_as_red_in_word_list)
    }

    private fun goToMainMenu() {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        return
    }
}

