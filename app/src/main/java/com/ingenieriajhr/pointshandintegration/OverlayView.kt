/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ingenieriajhr.pointshandintegration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.sax.EndElementListener
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmark
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlin.concurrent.thread
import kotlin.math.max

class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs),PlaySound {

    //save results recognized hands
    private var results: HandLandmarkerResult? = null
    //paints
    private var linePaint = Paint()
    private var pointPaint = Paint()

    //scale factor
    private var scaleFactor: Float = 1f
    //width and height the image
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    //Shooting Game declare
    private var shootingGame: ShootingGame? = null

    //init elements
    var initElementRunnin = false

    //sound systems game
    var snPlay = SnPlay(context!!)

    //var end sound laser
    var endSoundLaser = true

    var endSoundExplode = true


    init {
        initPaints()
    }

    /**
     * clear elements draw
     */
    fun clear() {
        results = null
        linePaint.reset()
        pointPaint.reset()
        invalidate()
        initPaints()
    }


    /**
     * init paints
      */
    private fun initPaints() {


        linePaint.color =
            ContextCompat.getColor(context!!, R.color.blue)
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE

        pointPaint.color = Color.RED
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL
    }

    /**
     * draw elements result and shooting, asteroids
     */
    override fun draw(canvas: Canvas) {
        super.draw(canvas)


        var point_THUMB_TIP = Pair(0f,0f)
        var point_THUMB_IP = Pair(0f,0f)

        var point_FINGER_DIP = Pair(0f,0f)
        var point_FINGER_TIP = Pair(0f,0f)

        results?.let { handLandmarkerResult ->
            val lines = mutableListOf<Float>()
            val points = mutableListOf<Float>()

            for (landmarks in handLandmarkerResult.landmarks()) {
                for (i in landmarkConnections.indices step 2) {
                    val startX =
                        landmarks[landmarkConnections[i]].x() * imageWidth * scaleFactor
                    val startY =
                        landmarks[landmarkConnections[i]].y() * imageHeight * scaleFactor
                    val endX =
                        landmarks[landmarkConnections[i + 1]].x() * imageWidth * scaleFactor
                    val endY =
                        landmarks[landmarkConnections[i + 1]].y() * imageHeight * scaleFactor
                    lines.add(startX)
                    lines.add(startY)
                    lines.add(endX)
                    lines.add(endY)
                    points.add(startX)
                    points.add(startY)

                    //if pulgar
                    if (landmarkConnections[i] == HandLandmark.THUMB_IP){
                        point_THUMB_IP = point_THUMB_IP.copy(first = landmarks[landmarkConnections[i]].x()).
                        copy(second = landmarks[landmarkConnections[i]].y())
                        point_THUMB_TIP = point_THUMB_TIP.copy(first = landmarks[landmarkConnections[i+1]].x()).
                        copy(second = landmarks[landmarkConnections[i+1]].y())

                        if (shootingGame!=null){
                            shootingGame!!.point_THUMB_IP = point_THUMB_IP
                            shootingGame!!.point_THUMB_TIP = point_THUMB_TIP
                        }
                    }
                    //if indice
                    if (landmarkConnections[i] == HandLandmark.INDEX_FINGER_DIP){
                        point_FINGER_DIP = point_FINGER_DIP.copy(first =
                        landmarks[landmarkConnections[i]].x()* imageWidth * scaleFactor).
                        copy(second = landmarks[landmarkConnections[i]].y()*imageHeight * scaleFactor)

                        point_FINGER_TIP = point_FINGER_DIP.copy(first =
                        landmarks[landmarkConnections[i+1]].x()* imageWidth * scaleFactor).
                        copy(second = landmarks[landmarkConnections[i+1]].y()*imageHeight * scaleFactor)

                        //update values
                        if (shootingGame!=null){
                            shootingGame!!.point_FINGER_DIP = point_FINGER_DIP
                            shootingGame!!.point_FINGER_TIP = point_FINGER_TIP
                            //shootingGame!!.updateShooting()
                        }
                    }

                }
                canvas.drawLines(lines.toFloatArray(), linePaint)
                canvas.drawPoints(points.toFloatArray(), pointPaint)
            }
            //draw elements shootingGame class
            if (shootingGame!=null){
                shootingGame!!.drawViewsAndShooting(canvas)
            }
        }
    }


    /**
     * @param handLandmarkerResults results mediapipe
     * @param imageHeight image height
     * @param imageWidth image width
     * @param runningMode default Running live_stream
     */
    fun setResults(
        handLandmarkerResults: HandLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.LIVE_STREAM
    ) {
        results = handLandmarkerResults

        if (!initElementRunnin){
            this.imageHeight = imageHeight
            this.imageWidth = imageWidth

            scaleFactor = when (runningMode) {
                RunningMode.LIVE_STREAM -> {
                    // PreviewView is in FILL_START mode. So we need to scale up the
                    // landmarks to match with the size that the captured images will be
                    // displayed.
                    max(width * 1f / imageWidth, height * 1f / imageHeight)
                }
                else -> {
                    max(width * 1f / imageWidth, height * 1f / imageHeight)
                }
            }
            initElementRunnin = true
        }

        if (shootingGame!=null && shootingGame!!.widthImageView == 0){
            shootingGame!!.widthImageView = imageWidth
            shootingGame!!.heightImageView = imageHeight
            shootingGame!!.scaleFactor = scaleFactor
        }

        invalidate()
    }

    /**
     * focus display get width and height view OverlayView
     */
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (shootingGame ==null) {
                shootingGame = ShootingGame(width, height, resources)
                shootingGame!!.initViewsAndClass()
            shootingGame!!.listenerPlay(this)
        }
    }




    /**
     * all sounds system
     */
    override fun play(nameSound: String) {

        snPlay.listenerSound(object :EndSound{
            override fun endSn(ifEndSound: Boolean) {
                endSoundLaser = ifEndSound
            }

            override fun endSnExploded(ifEndSound: Boolean) {
                endSoundExplode = ifEndSound
            }
        })

        when(nameSound){
            "laser"->{
                if (endSoundLaser){
                    thread(start = true){
                        snPlay.playLaser()
                    }
                }

            }
            "explode"->{
                if (endSoundExplode){
                    thread(start = true){
                        snPlay.playExplosion()
                    }
                }
            }
        }
    }





    companion object {
        private const val LANDMARK_STROKE_WIDTH = 12F

        // This list defines the lines that are drawn when visualizing the hand landmark detection
        // results. These lines connect:
        // landmarkConnections[2*n] and landmarkConnections[2*n+1]
        private val landmarkConnections = listOf(
            HandLandmark.WRIST,
            HandLandmark.THUMB_CMC,

            HandLandmark.THUMB_CMC,
            HandLandmark.THUMB_MCP,

            HandLandmark.THUMB_MCP,
            HandLandmark.THUMB_IP,

            HandLandmark.THUMB_IP,
            HandLandmark.THUMB_TIP,

            HandLandmark.WRIST,
            HandLandmark.INDEX_FINGER_MCP,

            HandLandmark.INDEX_FINGER_MCP,
            HandLandmark.INDEX_FINGER_PIP,

            HandLandmark.INDEX_FINGER_PIP,
            HandLandmark.INDEX_FINGER_DIP,

            HandLandmark.INDEX_FINGER_DIP,
            HandLandmark.INDEX_FINGER_TIP,

            HandLandmark.INDEX_FINGER_MCP,
            HandLandmark.MIDDLE_FINGER_MCP,

            HandLandmark.MIDDLE_FINGER_MCP,
            HandLandmark.MIDDLE_FINGER_PIP,

            HandLandmark.MIDDLE_FINGER_PIP,
            HandLandmark.MIDDLE_FINGER_DIP,

            HandLandmark.MIDDLE_FINGER_DIP,
            HandLandmark.MIDDLE_FINGER_TIP,

            HandLandmark.MIDDLE_FINGER_MCP,
            HandLandmark.RING_FINGER_MCP,

            HandLandmark.RING_FINGER_MCP,
            HandLandmark.RING_FINGER_PIP,

            HandLandmark.RING_FINGER_PIP,
            HandLandmark.RING_FINGER_DIP,

            HandLandmark.RING_FINGER_DIP,
            HandLandmark.RING_FINGER_TIP,

            HandLandmark.RING_FINGER_MCP,
            HandLandmark.PINKY_MCP,

            HandLandmark.WRIST,
            HandLandmark.PINKY_MCP,

            HandLandmark.PINKY_MCP,
            HandLandmark.PINKY_PIP,

            HandLandmark.PINKY_PIP,
            HandLandmark.PINKY_DIP,

            HandLandmark.PINKY_DIP,
            HandLandmark.PINKY_TIP
        )
    }




}
