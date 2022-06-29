package com.pitts.photo_detector

/*findViewById<ImageView>(R.id.came_back).setOnClickListener {
    finish()
}*/

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pitts.photo_detector.databinding.ActivityCameraBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit


class CameraActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCameraBinding

    private var imageCapture: ImageCapture? = null

/*    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null*/

    private var isAnalysisToggled: Boolean = false

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var pytorchModule: PytorchModule

    private lateinit var imageFilter: (Bitmap?) -> Bitmap?


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
/*        // Copy pth file to cache for future uses
        var cacheFile: File = run {
            File(this.cacheDir, "testmodel.pth")
                .also {
                    it.outputStream().use { cache ->
                        this.assets.open("testmodel.pth").use {
                            it.copyTo(
                                cache
                            )
                        }
                    }
                }
        }*/

        // Load the model file into torch model
        try {
            pytorchModule = PytorchModule(this, "model.ptl")
        } catch (e: IOException) {
            Log.e("TORCH", "Cannot found pth file from assets folder :(")
            finish()
        }


        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.cameShoot.setOnClickListener { takePhoto() }
        viewBinding.cameBack.setOnClickListener { finish() }
        viewBinding.cameAnalysisToggle.setOnClickListener {
            isAnalysisToggled = !isAnalysisToggled
            viewBinding.cameImageView.visibility =
                if (isAnalysisToggled) {
                    Toast.makeText(this, "ANALYSIS TOGGLED ON", Toast.LENGTH_SHORT).show()
                    View.VISIBLE
                } else {
                    Toast.makeText(this, "ANALYSIS TOGGLED OFF", Toast.LENGTH_SHORT).show()
                    View.INVISIBLE
                }
        }
        /* viewBinding.videoCaptureButton.setOnClickListener { captureVideo() }*/

        /**
         * Customizable Anonymous Function for Real-Time Image Processing
         * Here can we implement Python module to do real-time image processing
         * by using an existing PyObject available in this activity
         *
         * For simplicity, implementations for Python module is yet to be made
         *
         * @param (this) Bitmap to be processed
         * @return (context last line) Floatarray processed
         */
        imageFilter = {
            if (isAnalysisToggled) {
                it?.also {
                    val receivedFloatArray = pytorchModule.runInference(it)
                    Log.i("TORCH", receivedFloatArray.toString())
                    receivedFloatArray.forEach { Log.i("TORCH", it.toString()) }
                }
            } else {
                it
            }
            //Add your code here if you want to modify Bitmap Contents
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    /* private fun captureVideo() {}*/


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.camePreviewView.surfaceProvider)
                }

            val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                .build()
                .also { it ->
                    it.setAnalyzer(ContextCompat.getMainExecutor(this)) {
                        val processedBitmap: Bitmap? =
                            imageFilter.invoke(viewBinding.camePreviewView.bitmap)
                        val encodedString: String? = Miscellaneous.BitmapToString(processedBitmap)
                        encodedString?.let {
                            Log.i("DECODING", it)
                        }
                        it.close()
                        processedBitmap?.let {
                            runOnUiThread {
                                viewBinding.cameImageView.setImageBitmap(it)
                            }
                        }
                    }
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
/*                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview)*/
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalysis
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))

        imageCapture = ImageCapture.Builder().build()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

}