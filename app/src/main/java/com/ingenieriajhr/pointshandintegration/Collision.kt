package com.ingenieriajhr.pointshandintegration

import kotlin.math.abs

class Collision() {

    var yEvalueResultEquetion = 0f
    var xEvalueResultEquetion = 0f

    fun evaluate(
        y: Float, m: Float, b: Float, x: Float,
        posXAsteroids: Float, posYAsteroids: Float, widthRock: Float, heightRock: Float
    ):Boolean{


        //x position evalue
        xEvalueResultEquetion = evaluateCollisionX(posYAsteroids,b,m)
        //y position evalue
        yEvalueResultEquetion = evaluateCollisionY(m,xEvalueResultEquetion,b)

        var valuewResponse = false

        if (xEvalueResultEquetion>posXAsteroids && xEvalueResultEquetion<posXAsteroids+widthRock){
            valuewResponse =true
             if(yEvalueResultEquetion>posYAsteroids && yEvalueResultEquetion<posYAsteroids+heightRock){
                valuewResponse =true

            }
        }

        return valuewResponse
    }

    /**
     * return position in position Y
     */
    private fun evaluateCollisionX(posYAsteroids: Float, b: Float, m: Float): Float {
        return (posYAsteroids-b)/m
    }

    /**
     * return position in position X
     */
    private fun evaluateCollisionY(m: Float, posXAsteroids: Float, b: Float): Float {
        return  m*posXAsteroids+b
    }


}