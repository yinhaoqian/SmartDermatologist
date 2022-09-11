package com.pitts.photo_detector

import android.content.Context

class module_param {
    companion object {
        //PYTORCH_MOBILE:
        private var pytorchModelFileName: String = "alex.ptl"
        private var indexMappingCsvFileName: String = "index_mapping.csv"
        public var bitmapScaleFactor: Pair<Int, Int> = Pair(224, 224)
        fun initParametersByDefault(context: Context) {
            module_pytorch.loadModule(context, pytorchModelFileName)
            module_mapping.init(context, indexMappingCsvFileName)
        }

        //TENSORFLOW_LITE:
        var tensorflow_param_threshold: Float = 0.5f
        var tensorflow_param_maxResults: Int = 2
        var tensorflow_param_modelName: String = "mobilenetv1.tflite"
        var tensorflow_param_currentDelegate: Int = 0

    }
}