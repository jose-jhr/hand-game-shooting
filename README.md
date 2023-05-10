# HandMediapipeJhr


descargar el archivo task en el siguiente enlace(1) o el archivo zip (2) 
1) https://cienciayculturacreativa.com/mediapipejhr/hand_landmarker.task

2) [hand_landmarker.zip](https://github.com/jose-jhr/HandMediapipeJhr/files/11366419/hand_landmarker.zip)



//habilitar la vinculación de vistas en Android
```kotlin

 buildFeatures{
        viewBinding = true
    }

```
```kotlin
   
   //library CameraX
    implementation 'com.github.jose-jhr:Library-CameraX:1.0.8'
    //camera preview
    implementation "androidx.camera:camera-view:1.3.0-alpha06"
    // MediaPipe Library
    implementation 'com.google.mediapipe:tasks-vision:0.1.0-alpha-8'
    //implementation mediaPipeJhrLibrary
    implementation 'com.github.jose-jhr:HandMediapipeJhr:1.0.0'

```

Class draw result points


```kotlin


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmark
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlin.math.max
import kotlin.math.min

class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var results: HandLandmarkerResult? = null
    private var linePaint = Paint()
    private var pointPaint = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    init {
        initPaints()
    }
    fun clear() {
        results = null
        linePaint.reset()
        pointPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        linePaint.color =
            ContextCompat.getColor(context!!, R.color.blue)
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE

        pointPaint.color = Color.RED
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
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
                }
                canvas.drawLines(lines.toFloatArray(), linePaint)
                canvas.drawPoints(points.toFloatArray(), pointPaint)
            }
        }
    }

    fun setResults(
        handLandmarkerResults: HandLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = handLandmarkerResults

        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        scaleFactor =  max(width * 1f / imageWidth, height * 1f / imageHeight)

        invalidate()
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 12F
        
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



```


Class MainActivity Principal


```kotlin
class MainActivity : AppCompatActivity(),HandLandmarkerHelper.LandmarkerListener {
    lateinit var binding: ActivityMainBinding

    lateinit var cameraJhr: CameraJhr

    lateinit var mainViewModel:MainViewModel

    lateinit var handLandmarkerHelper:HandLandmarkerHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraJhr = CameraJhr(this)

        configHandLandMarker()
        handLandmarkerHelperinit()

    }



    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (cameraJhr.allpermissionsGranted()&&!cameraJhr.ifStartCamera){
            starCameraJhr()
        }else{
            cameraJhr.noPermissions()
        }
    }

    private fun starCameraJhr() {
        cameraJhr.addlistenerBitmap(object :BitmapResponse{
            override fun bitmapReturn(bitmap: Bitmap?) {
               handLandmarkerHelper.detectLiveStream(bitmap!!,true)
            }
        })

        cameraJhr.addlistenerImageProxy(object :ImageProxyResponse{
            override fun imageProxyReturn(imageProxy: ImageProxy) {
                try {

                
                }catch (e: IllegalStateException) {
                    // Handle the exception here
                    println("error en conversion imageproxy")
                }
            }
        })
        cameraJhr.initBitmap()
        cameraJhr.initImageProxy()

        cameraJhr.start(0,0,binding.cameraPreview,true,false,true)

    }

    fun Bitmap.rotate(degrees:Float)= Bitmap.createBitmap(this,0,0,width,height, Matrix().apply {
        postRotate(degrees)
    },true)



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

    override fun onError(error: String, errorCode: Int) {

    }

    override fun onResults(resultBundle: HandLandmarkerHelper.ResultBundle) {

            binding.overlay.setResults(
                resultBundle.results.first(),
                imageHeight = resultBundle.inputImageHeight,
                imageWidth = resultBundle.inputImageWidth,
                runningMode = RunningMode.LIVE_STREAM
            )

        binding.overlay.invalidate()
    }


}


```

![Screenshot_20230430-024128_PointsHandIntegration](https://user-images.githubusercontent.com/66834393/235341765-f5e1f263-7c0d-447e-a876-ca7fc7dbb946.jpg)

