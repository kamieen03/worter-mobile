package com.worter

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.drawable.PaintDrawable
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.get
import com.worter.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import android.view.ViewGroup
import android.graphics.drawable.shapes.RectShape
import android.graphics.Shader
import android.graphics.drawable.ShapeDrawable.ShaderFactory
import kotlin.math.min


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val dbManager = DBManager(this)
    private var selectedFile = "1"


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        dbManager.copyDbFromAssetsToDevice()
        dbManager.readDbFromDevice()
        fillFileList()
        setButtonOnClickListeners()
    }

    private fun fillFileList() {
        for (fName in dbManager.getFileNames()) {
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
            it.background = getFileButtonGradient(it.text as String)
        }
        fb.setBackgroundResource(R.drawable.active_fb)
        selectedFile = fb.text as String
    }

    private fun getFileButtonGradient(fName: String): PaintDrawable {
        val hardness = dbManager.getHardness(fName)
        val sf = getShader(hardness)
        val pd = PaintDrawable()
        pd.shape = RectShape()
        pd.setCornerRadius(25f)
        pd.shaderFactory = sf
        return pd
    }

    private fun getShader(hardness: Double): ShaderFactory {
        val learned = ContextCompat.getColor(this, R.color.learned)
        val notLearned = ContextCompat.getColor(this, R.color.not_yet_learned)
        val h = min(hardness, 5.0).toFloat()
        val middleColor = if (h <= 3.0) learned else notLearned
        val middlePosition = if (h <= 3.0) (h-1)/2 else (h-3)/2
        val sf = object : ShaderFactory() {
            override fun resize(width: Int, height: Int): Shader {
                return LinearGradient(
                    0f, 0f, width.toFloat(), 0f,
                    intArrayOf(notLearned, middleColor, learned),
                    floatArrayOf(0f, middlePosition, 1f),  // start, center and end position
                    Shader.TileMode.CLAMP
                )
            }
        }
        return sf
    }

    private fun setButtonOnClickListeners() {
        button_consecutive.setOnClickListener { startReviewActivity("CONSECUTIVE") }
        button_random.setOnClickListener { startReviewActivity("RANDOM") }
    }

    private fun startReviewActivity(mode: String) {
        val intent = Intent(this, ReviewActivity::class.java)
        intent.putExtra("mode", mode)
        intent.putExtra("fileName", selectedFile)
        startActivity(intent)
    }
}
