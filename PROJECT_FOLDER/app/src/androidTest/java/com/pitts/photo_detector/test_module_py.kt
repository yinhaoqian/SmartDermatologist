package com.pitts.photo_detector

import androidx.test.InstrumentationRegistry
import com.pitts.photo_detector.module_param.Companion.pytorch_param_modelName
import com.pitts.photo_detector.module_pytorch.Companion.loadModule
import org.junit.Before
import org.junit.Test

class test_module_py {
    private val appContext = InstrumentationRegistry.getTargetContext()
    private val androidTestContext = InstrumentationRegistry.getContext()

    @Before
    public fun initializeParameters() {
/*        initClassificationMapping.invoke(appContext)*/
        loadModule(appContext, pytorch_param_modelName)
    }

//    @Test
//    fun demoAppTest() {
//        val fileNames: Array<String> = arrayOf(
//            "TEST_PICTURES/64x72/BCC (1).jpg",
//            "TEST_PICTURES/64x72/BCC (2).jpg",
//            "TEST_PICTURES/64x72/BCC (3).jpg",
//            "TEST_PICTURES/64x72/BCC (4).jpg",
//            "TEST_PICTURES/64x72/BCC (5).jpg",
//            "TEST_PICTURES/64x72/MEL (1).jpg",
//            "TEST_PICTURES/64x72/MEL (2).jpg",
//            "TEST_PICTURES/64x72/MEL (3).jpg",
//            "TEST_PICTURES/64x72/MEL (4).jpg",
//            "TEST_PICTURES/64x72/MEL (5).jpg",
//            "TEST_PICTURES/64x72/SCC (1).jpg",
//            "TEST_PICTURES/64x72/SCC (2).jpg",
//            "TEST_PICTURES/64x72/SCC (3).jpg",
//            "TEST_PICTURES/64x72/SCC (4).jpg",
//            "TEST_PICTURES/64x72/SCC (5).jpg",
//        )
//        fileNames.forEach {
//            val bitmap = module_imageprocessing.obtainBitmapFromAsset(androidTestContext, it)
//            val score = module_pytorch.runInference(bitmap, androidTestContext)
//        }
//    }

    @Test
    fun dogAndCat() {
        val fileNames: Array<String> = arrayOf(
//            "TEST_PICTURES/224x224/cat1.png",
//            "TEST_PICTURES/224x224/cat2.png",
//            "TEST_PICTURES/224x224/dog1.png",
//            "TEST_PICTURES/224x224/dog2.png",
            "TEST_PICTURES/224x224/cat3.jpg"
        )
        fileNames.forEach {
            val bitmap = module_imageprocessing.obtainBitmapFromAsset(androidTestContext, it)
            val score = module_pytorch.runInference(bitmap, androidTestContext)
        }
    }

}