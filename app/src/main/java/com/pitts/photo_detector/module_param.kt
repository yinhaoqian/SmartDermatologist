package com.pitts.photo_detector

import android.content.Context

class module_param {
    companion object {
        private var pytorchModelFileName: String = "skin_model.ptl"
        private var indexMappingCsvFileName: String = "index_mapping.csv"
        fun initParametersByDefault(context: Context) {
            module_pytorch.loadModule(context, pytorchModelFileName)
            module_mapping.init(context, indexMappingCsvFileName)
        }
    }
}