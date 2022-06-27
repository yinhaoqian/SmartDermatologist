package com.pitts.photo_detector

import android.content.Context
import android.graphics.Bitmap
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class PytorchModule() {
    private var ready: Boolean = false
    private lateinit var ptModule: Module

    fun loadModel(context: Context, filePath: String): Unit {
        ptModule = Module.load(assetFilePath(context, filePath))
        ready = true
    }

    fun runInference(bitmap: Bitmap): FloatArray {
        val TENSOR_INPUT = bitmapToTensors(bitmap)
        val TENSOR_OUTPUT = runInferenceTensor2Tensor(TENSOR_INPUT)
        val FARRAY_OUTPUT = tensorsToFloatArray(TENSOR_OUTPUT)
        return FARRAY_OUTPUT
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
        if (ready.not()) throw Exception("Pytorch Module is NOT Initialized.")
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