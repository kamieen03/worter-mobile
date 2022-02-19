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
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        button_review_menu.setOnClickListener { runActivity<ReviewMenuActivity>() }
        button_all_words_list_menu.setOnClickListener { runActivity<AllWordsListMenuActivity>() }
        button_texts_menu.setOnClickListener { runActivity<TextsMenuActivity>() }
        button_listening_menu.setOnClickListener { runActivity<ListeningMenuActivity>() }
    }

    private inline fun <reified T> runActivity() {
        val intent = Intent(this, T::class.java)
        startActivity(intent)
    }
}
