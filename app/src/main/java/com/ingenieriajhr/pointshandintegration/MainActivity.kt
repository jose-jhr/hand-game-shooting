package com.ingenieriajhr.pointshandintegration

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Matrix
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.ingenieriajhr.handsmediapipejhr.HandLandmarkerHelper
import com.ingenieriajhr.handsmediapipejhr.MainViewModel
import com.ingenieriajhr.pointshandintegration.databinding.ActivityMainBinding
import com.ingenieriiajhr.jhrCameraX.BitmapResponse
import com.ingenieriiajhr.jhrCameraX.CameraJhr


class MainActivity : AppCompatActivity(),HandLandmarkerHelper.LandmarkerListener{


    lateinit var binding : ActivityMainBinding
    lateinit var cameraJhr: CameraJhr

    lateinit var handLandmarkerHelper:HandLandmarkerHelper
    lateinit var mainViewModel:MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this,R.color.purple_500)


        //init cameraJHR
        cameraJhr = CameraJhr(this)


        //init configuration handlandmarker
        configHandLandMarker()
        //initObject handLandMarkerHelper
        handLandmarkerHelperinit()
    }


    private fun configHandLandMarker() {
        mainViewModel = MainViewModel()
        mainViewModel.setDelegate(HandLandmarkerHelper.DELEGATE_GPU)
        mainViewModel.setMinHandDetectionConfidence(0.5f)
        mainViewModel.setMinHandPresenceConfidence(0.5f)
        mainViewModel.setMinHandTrackingConfidence(0.5f)
        mainViewModel.setMaxHands(1)
    }

    /**
     * init constructor handlandMarker
     */
    private fun handLandmarkerHelperinit() {
        handLandmarkerHelper = HandLandmarkerHelper(
            context = this,
            minHandDetectionConfidence = mainViewModel.currentMinHandDetectionConfidence,
            minHandTrackingConfidence = mainViewModel.currentMinHandTrackingConfidence,
            maxNumHands = mainViewModel.currentMaxHands,
            mP_HAND_LANDMARKER_TASK = "hand_landmarker.task",
            minHandPresenceConfidence = mainViewModel.currentMinHandPresenceConfidence,
            handLandmarkerHelperListener = this,
            currentDelegate = HandLandmarkerHelper.DELEGATE_GPU
        )
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (cameraJhr.allpermissionsGranted() && !cameraJhr.ifStartCamera){
            startCameraJhr()
        }else{
            cameraJhr.noPermissions()
        }
    }

    /**
     * start Camera Jhr
     */
    private fun startCameraJhr() {
        cameraJhr.addlistenerBitmap(object : BitmapResponse {
            override fun bitmapReturn(bitmap: Bitmap?) {
                if (bitmap!=null){
                    runOnUiThread {
                       //binding.imgBitmap.setImageBitmap(bitmap)
                    }

                    handLandmarkerHelper.detectLiveStream(bitmap,true)
                }
            }
        })
        cameraJhr.initBitmap()
        //selector camera LENS_FACING_FRONT = 0;    LENS_FACING_BACK = 1;
        //aspect Ratio  RATIO_4_3 = 0; RATIO_16_9 = 1;
        cameraJhr.start(0,0,binding.cameraPreview,true,false,true)
    }


    /**
     * @return bitmap rotate degrees
     */
    fun Bitmap.rotate(degrees:Float) = Bitmap.createBitmap(this,0,0,width,height,
        Matrix().apply { postRotate(degrees)
                       postScale(-1f,1f)},true)


    override fun onError(error: String, errorCode: Int) {

    }

    override fun onResults(resultBundle: HandLandmarkerHelper.ResultBundle) {

        binding.overlay.setResults(
            resultBundle.results.first(),
            imageHeight = resultBundle.inputImageHeight,
            imageWidth = resultBundle.inputImageWidth,
            RunningMode.LIVE_STREAM
        )

        // Force a redraw
        binding.overlay.invalidate()
    }


}