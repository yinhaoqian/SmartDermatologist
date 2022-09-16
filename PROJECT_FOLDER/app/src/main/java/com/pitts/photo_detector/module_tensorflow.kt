package com.pitts.photo_detector

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class module_tensorflow {
    companion object {
        private lateinit var objectDetector: ObjectDetector

        /*        private lateinit var imageAnalyzer: ImageAnalysis.Analyzer*/
        private lateinit var imageProcessor: ImageProcessor
        fun initiate(context: Context) {
            var temp_imageRotation: Int = 0
            //PART1 - OBJECT DETECTION
            //Set the options for model, such as the prediction threshold, results set size, and optionally, hardware acceleration delegates:
            val optionsBuilder =
                ObjectDetector.ObjectDetectorOptions.builder()
                    .setScoreThreshold(module_param.tensorflow_param_threshold)
                    .setMaxResults(module_param.tensorflow_param_maxResults)
            //Use the settings from this object to construct a TensorFlow Lite ObjectDetector object that contains the model:
            objectDetector = ObjectDetector.createFromFileAndOptions(
                context,
                module_param.tensorflow_param_modelName,
                optionsBuilder.build()
            )
            //Delegate SKIPPED!!!
            //Complete any final data transformations and add the image data to a TensorImage object, as shown in the ObjectDetectorHelper.detect() method of the example app:
            imageProcessor = ImageProcessor.Builder().add(Rot90Op(-temp_imageRotation / 90)).build()
        }

        fun runInference(bitmap: Bitmap): Unit {
            //Complete any final data transformations and add the image data to a TensorImage object, as shown in the ObjectDetectorHelper.detect() method of the example app:
            val tensorImageInput = imageProcessor.process(TensorImage.fromBitmap(bitmap))
            //Run the prediction by passing the image data to your predict function:
            val inferenceResult = objectDetector.detect(tensorImageInput)
            inferenceResult.forEach {
                it.categories.forEachIndexed { index, category ->
                    Log.d("Q_MODULE_TENSORFLOW", "RUNINFERENCE(): SCORE[$index]=${category.score} - ${category.displayName}")
                }
            }
        }
    }
}