package com.pitts.photo_detector

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.util.*
import java.util.concurrent.*

class module_pytorch() {

    companion object {
        private var isReady: Boolean = false
        private lateinit var ptModule: Module
        fun loadModule(context: Context, modelName: String) {
            ptModule =
                if (module_param.pytorch_param_useLiteLoader) {
                    throw RuntimeException()
                    /* LiteModuleLoader.load(module_imageprocessing.assetFilePath(context, modelName))*/
                } else {
                    Module.load(module_imageprocessing.assetFilePath(context, modelName))
                }

            Log.d("Q_PYTORCH", "loadModule -modelName modelName SUCCESS.")
            isReady = true
        }

        fun runInference(bitmap: Bitmap, context: Context): Int {
            if (!isReady) {
                Log.d("Q_PYTORCH", "runInference FAILED::ModelNotLoaded}.")
                throw RuntimeException()
            }

            //记录开始方法被叫时间
            var timeStamp: Long = Calendar.getInstance().timeInMillis
            //将Bitmap转换成Tensor以便输入
            val tensorInput = TensorImageUtils.bitmapToFloat32Tensor(
                bitmap,
                floatArrayOf(0.0f, 0.0f, 0.0f),
                floatArrayOf(1.0f, 1.0f, 1.0f),
            )
            //将转换好的输入Tensor进行计算，接受一个输出Tensor
            val tensorOutput = ptModule.forward(IValue.from(tensorInput)).toTensor()
            //将输出的Tensor转换成Array<Float>以比较大小
            val floatArrayOutput = tensorOutput.dataAsFloatArray
            floatArrayOutput.forEachIndexed { index, fl ->
                Log.d("Q_PYTORCH", "runInference @floatArrayOutput[${index}] = $fl ")
            }
            //记录方法计算结束时间点，并取差得到运行时间
            timeStamp = Calendar.getInstance().timeInMillis - timeStamp
            Log.d("Q_PYTORCH", "runInference @timeStamp = $timeStamp ")
            //将最大分数的索引值记录下来，返回给叫函数者
            val indexOfMaxScore =
                floatArrayOutput.indexOfFirst { it == floatArrayOutput.maxOrNull() }
            Log.d("Q_PYTORCH", "runInference @indexOfMaxScore = $indexOfMaxScore")
            return indexOfMaxScore
        }

        fun runTimedInferenceOnAnotherThread(bitmap: Bitmap, context: Context, timeout: Int): Int {
            var toReturn: Int? = null
            val callable = Callable {
                runInference(bitmap, context)
            }
            val scheduledExecutorService: ExecutorService = Executors.newScheduledThreadPool(2)
            val future = scheduledExecutorService.submit(callable)
            try {
                toReturn = future[timeout.toLong(), TimeUnit.SECONDS]
            } catch (e: ExecutionException) {
                e.printStackTrace()
                Log.e(
                    "Q_PYTORCH",
                    "runTimedInferenceOnAnotherThread FAILED::ExecutionException"
                )
            } catch (e: InterruptedException) {
                e.printStackTrace()
                Log.e(
                    "Q_PYTORCH",
                    "runTimedInferenceOnAnotherThread FAILED::InterruptedException"
                )
            } catch (e: TimeoutException) {
                future.cancel(true)
                Log.e("Q_PYTORCH", "runTimedInferenceOnAnotherThread FAILED::TimeoutException")
            }
            return if (toReturn === null) -1 else toReturn
        }
    }
}