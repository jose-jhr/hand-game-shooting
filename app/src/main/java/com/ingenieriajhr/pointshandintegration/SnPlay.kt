package com.ingenieriajhr.pointshandintegration

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import android.sax.EndElementListener


class SnPlay(val context: Context) {

    private var media: MediaPlayer = MediaPlayer()
    private var mediaExplosion: MediaPlayer = MediaPlayer()

    private lateinit var endSound :EndSound

    fun listenerSound(endSound: EndSound){
        this.endSound = endSound
    }

    fun playLaser(){
        media = MediaPlayer.create(context,R.raw.lasermejor)
        endSound.endSn(false)
        media.setOnCompletionListener {
            media.release()
            endSound.endSn(true)
        }
        media.start()
    }



    fun stopLaser(){
        media.reset()
    }


    fun playExplosion(){
        mediaExplosion = MediaPlayer.create(context,R.raw.explosion)
        endSound.endSnExploded(false)
        mediaExplosion.setOnCompletionListener {
            mediaExplosion.release()
            endSound.endSnExploded(true)
        }
        mediaExplosion.start()
    }



}