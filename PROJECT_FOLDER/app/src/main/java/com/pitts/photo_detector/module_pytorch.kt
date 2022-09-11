package com.pitts.photo_detector

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.util.*

class module_pytorch() {

    companion object {
        private var isReady: Boolean = false
        private lateinit var ptModule: Module
        fun loadModule(context: Context, modelName: String) {
            ptModule =
                LiteModuleLoader.load(module_imageprocessing.assetFilePath(context, modelName))
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
            val toReturn = TensorImageUtils.bitmapToFloat32Tensor(
                bitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB
            )
            return toReturn
        }

        private fun tensorsToFloatArray(tensor: Tensor): FloatArray {
            return tensor.dataAsFloatArray
        }

        private fun runInferenceTensor2Tensor(tensor: Tensor): Tensor {
            val ts1: Tensor = tensor
/*            ts1.dataAsFloatArray.forEachIndexed { index, fl ->
                if (index < 1000) {
                    Log.d("Q_TS1", "${index} _ ${fl}")
                }

            }*/
            val ts2: Tensor = ptModule.forward(IValue.from(ts1)).toTensor()
/*            ts2.dataAsFloatArray.forEachIndexed { index, fl ->
                Log.d("Q_TS1", "${index} _ ${fl}")
            }*/
            return ts2
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