package com.ingenieriajhr.pointshandintegration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.abs

class Shooting() {

    var posX = 0f
    var posY = 0f

    var y = 0f
    var x = 0f
    var m = 0f
    var b = 0f


    var paint: Paint = Paint()

    init {
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = Color.RED
        paint.strokeWidth = 5f
    }

    fun calculateEquationstraight(
        point1: Pair<Float, Float>,
        point2: Pair<Float, Float>
    ){
        m = (point1.second-point2.second)/(point1.first-point2.first)
        b = point1.second-m*point1.first

        //new point y = mx+b
        y = 0f
        x = (y-b)/m

        y = abs(y)

        posX = point1.first
        posY = point1.second
    }

    fun drawLaser(canvas: Canvas){
        canvas.drawLine(posX,posY,x,y,paint)
    }



}