package com.ingenieriajhr.pointshandintegration

import android.content.res.Resources
import android.graphics.Canvas
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.roundToInt

class ShootingGame(val widthView: Int, val heightView: Int, val resources: Resources) {

    //width and height rock
    private var widthRock = 0
    private var heightRock = 0

    //porcentage size
    private var porcentageWidthAsteroid = 0.1f
    private var porcentageHeightAsteroid = 0.05f

    var numAsteroids = 10

    //create object Asteroids
    private var asteroids = ArrayList<Asteroids>().apply {
        for (i in 0..numAsteroids){
            this.add(Asteroids(widthView,heightView))
        }
    }
    //private var asteroids = Asteroids(widthView,heightView)

    //point shooting
    var point_THUMB_TIP = Pair(0f,0f)
    var point_THUMB_IP = Pair(0f,0f)

    var point_FINGER_DIP = Pair(0f,0f)
    var point_FINGER_TIP = Pair(0f,0f)


    //save angle before
    private var angleBefore = 0f

    //all elements shooting
    var widthImageView = 0
    var heightImageView = 0
    var scaleFactor = 0f

    //object shooting
    val shooting = Shooting()

    //counter trigger
    var counterAnimationTrigger = 0

    //init object collision
    val collision = Collision()

    //interfaceSoundToOverlay
    lateinit var playSound: PlaySound


    fun initViewsAndClass(){
        widthRock = (widthView*porcentageWidthAsteroid).roundToInt()
        heightRock = (heightView*porcentageHeightAsteroid).roundToInt()
        asteroids.forEach {
            it.initBitmap(resources = resources,widthRock,heightRock)
        }
        //asteroids.initBitmap(resources = resources,widthRock,heightRock)
    }



    fun drawViewsAndShooting(canvas: Canvas){
        asteroids.forEach {
            it.drawRock(canvas)
        }
        //asteroids.drawRock(canvas)
        var triggerSave = trigger()

        if (triggerSave || counterAnimationTrigger!=0) {
            updateShooting()
            shooting.drawLaser(canvas)
            counterAnimationTrigger--
        }

        if (triggerSave){
            asteroids.forEach {
                val explode  = collision.evaluate(shooting.y,shooting.m,
                    shooting.b,shooting.x, it.posX,it.posY
                    ,it.widthRock,it.heightRock)
                if (explode) {
                    it.explodeAsteroids()
                    threaSoundPlay("explode")
                }
            }
            threaSoundPlay("laser")
           // playSound.play("laser")
           /*val explode  = collision.evaluate(shooting.y,shooting.m,
                shooting.b,shooting.x, asteroids.posX,asteroids.posY
                ,asteroids.widthRock,asteroids.heightRock)
            if (explode) asteroids.explodeAsteroids()*/
        }

    }

    private fun threaSoundPlay(s: String) {
        playSound.play("$s")
    }

    private fun trigger(): Boolean {

        //return value
        var angleChange = false
        //if points diferent 0
        if (point_THUMB_TIP.first !=0f) {
            //get angle points
            val angle = getAngle(point_THUMB_TIP, point_THUMB_IP)
            //change angle
            if (angle < 0 && angleBefore >= 0) {
                angleBefore = angle
                angleChange = true
                counterAnimationTrigger = 2
            }else{
                //change angle
                if (angle > 0 && angleBefore <= 0) {
                    angleBefore = angle
                    angleChange = true
                    counterAnimationTrigger = 2
                }
            }

        }
        return angleChange
    }

    fun getAngle(point1:Pair<Float,Float>,point2: Pair<Float,Float>): Float {
        //calculate angle two point degrees = arctan(y/x)*(180/PI)
        val y = point2.second - point1.second
        val x = point2.first - point1.first

        return (atan(y/x) *(180/ PI)).toFloat()
    }


    /**
     * all shooting
     */
    fun updateShooting(){
        shooting.calculateEquationstraight(point_FINGER_TIP,point_FINGER_DIP)
    }

    /**
     * listener method
     */
    fun listenerPlay(playSound: PlaySound){
        this.playSound = playSound
    }




}