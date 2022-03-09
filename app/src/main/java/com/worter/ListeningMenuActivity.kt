package com.worter

import android.annotation.SuppressLint
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.shapes.RectShape
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_listening_menu.*
import java.lang.ref.WeakReference
import java.util.*


enum class AudioType {
    SLOW,
    FAST
}

class ListeningMenuActivity : AppCompatActivity() {
    private val missingFastAudioEpisodes = intArrayOf(23, 34, 47, 71)
    private var missingEpisodesButtons: MutableList<Button> = mutableListOf()

    var selectedAudioType = AudioType.SLOW
        private set
    var selectedIdx: Int = 10
        private set
    private lateinit var audioController: AudioController

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listening_menu)
        audioController = AudioController(this)
        fillListeningList()
        listening_flip_audio_type_button.setOnClickListener { flipAudioType() }
        listening_flip_audio_listened_button.setOnClickListener { flipAudioListened() }
    }

    override fun onPause() {
        super.onPause()
        DBManager.saveListenedAudios()
        audioController.stopSound()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioController.stopTimer()
    }

    private fun fillListeningList() {
        for (idx in 10..245) {
            val fb = getTextsListButton(idx)
            listenigs_list.addView(fb)
            setFbMargin(fb)
            if (missingFastAudioEpisodes.contains(idx)) {
                missingEpisodesButtons.add(fb)
            }
        }
        (listenigs_list[0] as Button).callOnClick()
    }

    private fun getTextsListButton(idx: Int): Button {
        val fb = Button(this)
        fb.text = idx.toString()
        fb.textSize = 20f
        fb.setTextColor(ContextCompat.getColor(this, R.color.font))
        fb.setOnClickListener { listeningsTableButtonOnClickListener(fb) }
        return fb
    }

    private fun setFbMargin(fb: Button) {
        val marginParam = fb.layoutParams as ViewGroup.MarginLayoutParams
        marginParam.setMargins(0,20,0,20)
        fb.layoutParams = marginParam
    }

    private fun listeningsTableButtonOnClickListener(fb: Button) {
        audioController.stopSound()
        listenigs_list.children.forEach { it as Button
            it.background = getListeningButtonShape(it)
        }
        fb.background.setTint(ContextCompat.getColor(this, R.color.Bronze))
        selectedIdx = fb.text.toString().toInt()
        setAudioListenedButton(DBManager.isAudioListened(selectedIdx, selectedAudioType)!!)
    }

    private fun getListeningButtonShape(fb: Button): PaintDrawable {
        val isAudioListened = DBManager.isAudioListened(fb.text.toString().toInt(), selectedAudioType)!!
        val color = if (selectedAudioType == AudioType.FAST &&  missingEpisodesButtons.contains(fb)) {
            R.color.grey
        } else if (isAudioListened) {
            R.color.learned
        } else {
            R.color.not_yet_learned
        }
        val pd = PaintDrawable()
        pd.shape = RectShape()
        pd.setCornerRadius(25f)
        pd.setTint(ContextCompat.getColor(this, color))
        return pd
    }

    @SuppressLint("SetTextI18n")
    private fun flipAudioType() {
        audioController.stopSound()
        if (selectedAudioType == AudioType.SLOW) {
            selectedAudioType = AudioType.FAST
            listening_flip_audio_type_button.text = "FAST"
            listening_flip_audio_type_button.setTextColor(ContextCompat.getColor(this, R.color.dirty_white))
            listening_flip_audio_type_button.background.setTint(ContextCompat.getColor(this, R.color.Blood_Red))
            makeMissingEpisodesUnavailable()
        } else {
            selectedAudioType = AudioType.SLOW
            listening_flip_audio_type_button.text = "SLOW"
            listening_flip_audio_type_button.setTextColor(ContextCompat.getColor(this, R.color.font))
            listening_flip_audio_type_button.background.setTint(ContextCompat.getColor(this, R.color.learned))
            makeMissingEpisodesAvailable()
        }
    }

    private fun makeMissingEpisodesUnavailable() {
        for (button in missingEpisodesButtons) {
            button.setOnClickListener {}
        }

        if (missingFastAudioEpisodes.contains(selectedIdx)) {
           listenigs_list[selectedIdx - 10 - 1].callOnClick()
        } else {
            listenigs_list[selectedIdx - 10].callOnClick()
        }
    }

    private fun makeMissingEpisodesAvailable() {
        for (button in missingEpisodesButtons) {
            button.setOnClickListener { listeningsTableButtonOnClickListener(button) }
        }
        listenigs_list[selectedIdx - 10].callOnClick()
    }

    @SuppressLint("SetTextI18n")
    private fun setAudioListenedButton(isAudioListened: Boolean) {
        if (isAudioListened) {
            listening_flip_audio_listened_button.background.setTint(ContextCompat.getColor(this, R.color.learned))
            listening_flip_audio_listened_button.text = "LISTENED"
        } else {
            listening_flip_audio_listened_button.background.setTint(ContextCompat.getColor(this, R.color.not_yet_learned))
            listening_flip_audio_listened_button.text = "NOT LISTENED"
        }
    }

    private fun flipAudioListened() {
        DBManager.flipAudioListened(selectedIdx, selectedAudioType)
        setAudioListenedButton(DBManager.isAudioListened(selectedIdx, selectedAudioType)!!)
    }
}

class AudioController(context: ListeningMenuActivity) {
    private val REWIND_VAL = 10000
    private val SKIP_VAL = 10000

    private val ctx = WeakReference(context)
    private var audioPlayer: MediaPlayer? = null
    private val timer = Timer()

    init {
        // play / pause
        ctx.get()!!.listening_play_button.setOnClickListener {
            if (audioPlayer != null && audioPlayer!!.isPlaying) {
                pauseSound()
            } else {
                playSound()
            }
        }

        // rewind
        ctx.get()!!.listening_rewind_button.setOnClickListener {
            if (audioPlayer != null) {
                val newTime = maxOf(audioPlayer!!.currentPosition - REWIND_VAL, 0)
                audioPlayer!!.seekTo(newTime)
                ctx.get()!!.listening_seek_bar.progress = newTime
            }
        }

        // skip
        ctx.get()!!.listening_skip_button.setOnClickListener {
            if (audioPlayer != null) {
                val newTime = minOf(
                    audioPlayer!!.currentPosition + SKIP_VAL,
                    audioPlayer!!.duration
                )
                audioPlayer!!.seekTo(newTime)
                ctx.get()!!.listening_seek_bar.progress = newTime
            }
        }

        // update seekBar every second
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                ctx.get()!!.listening_seek_bar.progress = if (audioPlayer != null) {
                    audioPlayer!!.currentPosition
                } else 0
            }
        }, 0, 1000)

        ctx.get()!!.listening_seek_bar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, ms: Int, b: Boolean) {
                ctx.get()!!.listening_current_time.text = msToTimeString(ms)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun playSound() {
        if (audioPlayer == null) {
            audioPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(idxToUrl(ctx.get()!!.selectedIdx))
                setOnCompletionListener { pauseSound() }
                prepare()
                start()
            }
            ctx.get()!!.listening_seek_bar.max = audioPlayer!!.duration
            ctx.get()!!.listening_duration_time.text = msToTimeString(audioPlayer!!.duration)
        } else audioPlayer!!.start()

        ctx.get()!!.listening_play_button.setImageResource(android.R.drawable.ic_media_pause)
    }

    private fun pauseSound() {
        if (audioPlayer != null && audioPlayer!!.isPlaying) audioPlayer!!.pause()
        ctx.get()!!.listening_play_button.setImageResource(android.R.drawable.ic_media_play)
    }

    fun stopSound() {
        if (audioPlayer != null) {
            pauseSound()
            audioPlayer!!.stop()
            audioPlayer!!.release()
            audioPlayer = null
            ctx.get()!!.listening_duration_time.text = msToTimeString(0)
        }
    }

    fun stopTimer() {
        timer.cancel()
    }

    private fun idxToUrl(idx: Int): String? {
        return if (ctx.get()!!.selectedAudioType == AudioType.SLOW) {
            when (idx) {
                in 10..172 -> "https://kinderwahnsinn.com/folgen/sg${idx}.mp3"
                in 173..245 -> "https://cdn.podseed.org/slowgerman/sg${idx}.mp3"
                else -> null
            }
        } else {
            when (idx) {
                in 10..245 -> "https://slowgerman.com/premium/sg${idx}s.mp3"
                else -> null
            }
        }
    }

    private fun msToTimeString(ms: Int) : String{
        var s : Int = ms / 1000
        val min: Int = s / 60
        s %= 60
        val sec = if (s < 10) "0$s" else "$s"
        return "${min}:${sec}"
    }

}
