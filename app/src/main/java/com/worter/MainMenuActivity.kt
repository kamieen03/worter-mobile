package com.worter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.worter.databinding.ActivityMainMenuBinding
import kotlinx.android.synthetic.main.activity_main_menu.*

class MainMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainMenuBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        DBManager.setContext(this)
        button_review_menu.setOnClickListener { runReviewMenuActivity() }
        button_all_words_list_menu.setOnClickListener { runAllWordsListActivity() }
    }

    private fun runReviewMenuActivity() {
        val intent = Intent(this, ReviewMenuActivity::class.java)
        startActivity(intent)
    }

    private fun runAllWordsListActivity() {
        val intent = Intent(this, AllWordsListMenuActivity::class.java)
        startActivity(intent)
    }

}
