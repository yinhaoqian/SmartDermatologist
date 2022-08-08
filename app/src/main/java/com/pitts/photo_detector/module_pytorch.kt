package com.pitts.photo_detector

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

class module_pytorch(context: Context, modelName: String) {

    companion object {
        private var isReady: Boolean = false
        private lateinit var ptModule: Module
        fun loadModule(context: Context, modelName: String) {
            ptModule = LiteModuleLoader.load(assetFilePath(context, modelName))
            Log.d("Q_MODULE_PYTORCH", "LOADMODULE(): LITE MODEL SUCCESSFULLY MOUNTED.")
            isReady = true
        }

        fun runInference(bitmap: Bitmap, context: Context): Int {
            assert(isReady)
            var timeStamp: Long = Calendar.getInstance().timeInMillis
            val tensorInput = bitmapToTensors(bitmap)
            val tensorOutput = runInferenceTensor2Tensor(tensorInput)
            val fArrayOutput = tensorsToFloatArray(tensorOutput)
            fArrayOutput.forEachIndexed { index, fl ->
                Log.d("Q_MODULE_PYTORCH", "RUNINFERENCE(): OBTAINED SCORE[${index}] = $fl ")
            }
            timeStamp = Calendar.getInstance().timeInMillis - timeStamp
            Log.d("Q_MODULE_PYTORCH", "RUNINFERENCE(): TIME ELAPSED IS $timeStamp MILISECS")
            return locateMaxIndex(fArrayOutput)
        }

        private fun locateMaxIndex(arr: FloatArray): Int {
            return arr.indexOfFirst { it == arr.maxOrNull() }.also {
                Log.d("Q_MODULE_PYTORCH", "LOCATEMAXINDEX(): DECISION IS INDEX $it")
            }
        }

        private fun bitmapToTensors(bitmap: Bitmap): Tensor {
            return TensorImageUtils.bitmapToFloat32Tensor(
                bitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB
            )
        }

        private fun tensorsToFloatArray(tensor: Tensor): FloatArray {
            return tensor.dataAsFloatArray
        }

        private fun runInferenceTensor2Tensor(tensor: Tensor): Tensor {
            return ptModule.forward(IValue.from(tensor)).toTensor()
        }

        /**
         * https://stackoverflow.com/questions/59588556/androidkotlin-how-getting-assets-file-path-at-pytorch-mobile
         */
        private fun assetFilePath(context: Context, asset: String): String {
            val file = File(context.filesDir, asset)

            try {
                val inpStream: InputStream = context.assets.open(asset)
                try {
                    val outStream = FileOutputStream(file, false)
                    val buffer = ByteArray(4 * 1024)
                    var read: Int

                    while (true) {
                        read = inpStream.read(buffer)
                        if (read == -1) {
                            break
                        }
                        outStream.write(buffer, 0, read)
                    }
                    outStream.flush()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                return file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }
    }

/*    private var bitmap: Bitmap? = null
    private var floatArray: FloatArray? = null*/
/*    public var inferator: Runnable = Runnable {
        floatArray = bitmap?.run {
            bitmap = null
            runInference(this).also {
                Log.i("TORCH", it.toString())
            }
        }

    }*/

/*    init {

    }*/
/*
    fun writeBitmap(bitmap: Bitmap) {
        while (this.bitmap != null) {
            Log.d("TORCH", "Inject Task Scheduled but waiting")
        }
        this.bitmap = bitmap
    }

    fun readFloatArray(): FloatArray {
        while (this.floatArray == null) {
            Log.d("TORCH", "Extract Task Scheduled but waiting")
        }
        return this.floatArray!!.also {
            this.floatArray = null
        }
    }*/


}