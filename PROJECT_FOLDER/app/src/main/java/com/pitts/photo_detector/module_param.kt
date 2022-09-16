package com.pitts.photo_detector

import android.content.Context

class module_param {
    companion object {
        //索引对应分类参数（一般不需要调整）
        private var param_mapFileName: String = "index_mapping.csv"
        public val initClassificationMapping: (Context) -> Unit = {
            module_mapping.init(it, param_mapFileName)
        }

        //PYTORCH参数（按需求调整）
        public var pytorch_param_modelName: String = "resnet18.pt"
        public var pytorch_param_useLiteLoader: Boolean = false
        public var pytorch_param_useScaling: Boolean = true
        public var pytorch_param_useFilterWhileScaling: Boolean = false
        public var pytorch_param_bitmapScaleFactor: Pair<Int, Int> = Pair(224, 224)


        //TENSORFLOW参数（按需求调整）
        var tensorflow_param_threshold: Float = 0.5f
        var tensorflow_param_maxResults: Int = 2
        var tensorflow_param_modelName: String = "mobilenetv1.tflite"
        var tensorflow_param_currentDelegate: Int = 0

    }
}