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

class module_pytorch(context: Context, modelName: String) {
    private lateinit var ptModule: Module
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

    init {
        ptModule = LiteModuleLoader.load(assetFilePath(context, modelName))
    }
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

    fun runInference(bitmap: Bitmap): Int {
        val TENSOR_INPUT = bitmapToTensors(bitmap)
        val TENSOR_OUTPUT = runInferenceTensor2Tensor(TENSOR_INPUT)
        val FARRAY_OUTPUT = tensorsToFloatArray(TENSOR_OUTPUT)
        FARRAY_OUTPUT.forEach { Log.d("PYTORCH_ARRAY", it.toString()) }
        return locateMaxIndex(FARRAY_OUTPUT)
    }

    private fun locateMaxIndex(arr: FloatArray): Int {
        return arr.indexOfFirst { it == arr.maxOrNull() }
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