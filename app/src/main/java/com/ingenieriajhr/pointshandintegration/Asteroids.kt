package com.ingenieriajhr.pointshandintegration

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint

class Asteroids(val widthView: Int, val heightView: Int) {

    private lateinit var rockAsteroid: Bitmap
    private lateinit var explodeBitmap: Bitmap

    //width and height rock
    var widthRock = 0f
    var heightRock  = 0f
    //posX and posY Rock
    var posX = 0f
    var posY = 0f
    //paint Asteroids
    private var paint = Paint()
    //if startGame change Position
    var startAsteroids = true
    //gravity position
    var gravity = 0f
    //explosion
    var ifExplode = false
    //count explosion
    protected var countExplosion  = 10



    /**
     * @param resources open files
     * @param width and @param height porcentaje from overlayView
     */
    fun initBitmap(resources:android.content.res.Resources,width:Int,Height:Int){
        widthRock = width.toFloat()
        heightRock = Height.toFloat()
        gravity = heightRock*0.2f
        rockAsteroid = BitmapFactory.decodeResource(resources,R.mipmap.rock)
        rockAsteroid = Bitmap.createScaledBitmap(rockAsteroid,widthRock.toInt(),heightRock.toInt(),false)
        explodeBitmap = BitmapFactory.decodeResource(resources,R.mipmap.explode)
        explodeBitmap = Bitmap.createScaledBitmap(explodeBitmap,
            widthRock.toInt(), heightRock.toInt(),false)
        posX = randomPosX()
    }


    /**
     * @param canvas draw Bitmap in display in view Overlay
     */
    fun drawRock(canvas: Canvas){

        if (rockAsteroid!=null){
            if (startAsteroids) changePositionAsteroids()
            if (ifExplode){
                countExplosion--
                canvas.drawBitmap(explodeBitmap,posX,posY,paint)
            }else{
                canvas.drawBitmap(rockAsteroid,posX,posY,paint)
            }
        }
    }

    /**
     * change position Asteoroids
     */
    private fun changePositionAsteroids() {
        if(posY>heightView){
            randomPosX()
            clearPosition()
        }
        if (!ifExplode){
           posY+=gravity
        }

        if (ifExplode && countExplosion==0){
            ifExplode = false
            countExplosion = 10
            clearPosition()
        }
    }


    fun explodeAsteroids(){
        ifExplode = true
        countExplosion = 10
    }

    /**
     * clear posiontion x and y
     */
    private fun clearPosition() {
        posY = 0f
        posX = randomPosX()
    }

    private fun randomPosX() =  (0..(widthView-widthRock).toInt()).random().toFloat()


}