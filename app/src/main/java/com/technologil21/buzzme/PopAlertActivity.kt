package com.technologil21.buzzme

import android.app.NotificationManager
import android.graphics.drawable.AnimationDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_pop_alert.*


class PopAlertActivity : AppCompatActivity() {

    //gestion audio
    private var player: MediaPlayer? = null
    lateinit var audio: AudioManager
    private var canalAudio: Int = AudioManager.STREAM_MUSIC //STREAM_MUSIC est celui utilise par MediaPlayer
    private var originalVolume: Int = 50
    private var askedLevel = 90

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pop_alert)
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        window.setLayout((dm.widthPixels * .8).toInt(), (dm.heightPixels * .8).toInt())

        flash.setBackgroundResource(R.drawable.animation)
        var sosAnim: AnimationDrawable = flash.background as AnimationDrawable

        // On recupere l'intent qui a lance cette activite
        val i = intent
        //on recupere les donnees transmises par l'autre activite
        val message = i.getStringExtra(Global.NOTIF)
        val time = "affiché à " + i.getStringExtra(Global.HEURE)
        btn_close.setOnClickListener { finish() }
        msg.text = message
        heure.text = time
        sosAnim.start()

        audio = getSystemService(AUDIO_SERVICE) as AudioManager
        //si demande disableDND
        if (AppPreferences.disableDND) unmute()
        //si demande on joue le fichier audio d'alerte
        if (AppPreferences.soundOn) {
            play()
        }
    }

    //disableDND
    private fun unmute() {
        //Desactive Do Not Disturb
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Get the notification manager instance
            val mNotificationManager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            //Set the interruption filter
            mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL) //no notifications are suppressed
        }
        audio.adjustVolume(AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI)
    }

    //joue le fichier audio d'alerte
    private fun play() {
        askedLevel = AppPreferences.alertVol
        if (player == null) {
            //init du player
            player = MediaPlayer.create(this, R.raw.rencontres)
            player?.setOnCompletionListener { stopPlayer() }
        }
//        Toast.makeText(applicationContext, askedLevel.toString(), Toast.LENGTH_SHORT).show()
        originalVolume = audio.getStreamVolume(canalAudio) //memorisation du niveau de volume actuel
        audio.setStreamVolume(canalAudio, (askedLevel * audio.getStreamMaxVolume(canalAudio) / 100), 0)
        player!!.setOnPreparedListener { player!!.start() }
    }

    private fun stopPlayer() {
        if (player != null) {
            player?.release()
            player = null
        }
        audio.setStreamVolume(canalAudio, originalVolume, 0)
    }

    override fun onStop() {
        super.onStop()
        stopPlayer()
    }
}
