package com.pitts.photo_detector

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
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
            //记录开始方法被叫时间
            var timeStamp: Long = Calendar.getInstance().timeInMillis
            //将Bitmap转换成Tensor以便输入
            val tensorInput = TensorImageUtils.bitmapToFloat32Tensor(
                bitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB
            )
            //将转换好的输入Tensor进行计算，接受一个输出Tensor
            val tensorOutput = ptModule.forward(IValue.from(tensorInput)).toTensor()
            //将输出的Tensor转换成Array<Float>以比较大小
            val floatArrayOutput = tensorOutput.dataAsFloatArray
            floatArrayOutput.forEachIndexed { index, fl ->
                Log.d("Q_MODULE_PYTORCH", "RUNINFERENCE(): OBTAINED SCORE[${index}] = $fl ")
            }
            //记录方法计算结束时间点，并取差得到运行时间
            timeStamp = Calendar.getInstance().timeInMillis - timeStamp
            Log.d("Q_MODULE_PYTORCH", "RUNINFERENCE(): TIME ELAPSED IS $timeStamp MILISECS")
            //将最大分数的索引值记录下来，返回给叫函数者
            val indexOfMaxScore =
                floatArrayOutput.indexOfFirst { it == floatArrayOutput.maxOrNull() }
            Log.d("Q_MODULE_PYTORCH", "LOCATEMAXINDEX(): DECISION IS INDEX $indexOfMaxScore")
            return indexOfMaxScore
        }

    }
}