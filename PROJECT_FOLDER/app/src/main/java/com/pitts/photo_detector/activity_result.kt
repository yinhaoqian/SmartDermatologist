package com.pitts.photo_detector

import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class activity_result : AppCompatActivity() {
    private var allViews: MutableSet<View> = mutableSetOf()
    private var imageBuffer: Bitmap? = null
/*    private lateinit var transitionPack: Triple<Scene, Scene, Transition>*/


    private fun getResultIndexFromBitmap(bitmap: Bitmap): Int {
        /*        animateToDoneScene()*/
        return module_pytorch.runInference(bitmap, this).also {
            Log.d("Q_ACTIVITY_RESULT", "GETRESULTINDEXFROMBITMAP(): RECEIVED DECISION $it")
        }
    }


    /*
        private fun inferAndDisplay() {
            Log.d("RESULT_IAD", "inferAndDisplay called")
            Toast.makeText(this, "IAD INITIATED", Toast.LENGTH_SHORT).show()
            imageBuffer?.let {
                resultIndex = pytorchModule.runInference(it).inc()
                val returnedStringLocation: Pair<Int, Int>? = indexDict[resultIndex]
                findViewById<TextView>(R.id.text_result_diseaseTitle).text =
                    getString(returnedStringLocation?.first ?: R.string.disease_error)
                findViewById<TextView>(R.id.text_result_diseaseDetail).text =
                    getString(returnedStringLocation?.second ?: R.string.disease_error)
            }
            Log.d("RESULT_IAD", "inferAndDisplay ended")
            Toast.makeText(this, "IAD ENDED", Toast.LENGTH_SHORT).show()
        }
    */
    private fun displayDisease(diseaseIndexKey: Int) {
        module_mapping.getPairFromIndex(diseaseIndexKey).let {
            Log.d("Q_ACTIVITY_RESULT", "DISPLAYDISEASE(): OBTAINED ${it.first} , ${it.second}")
            findViewById<TextView>(R.id.text_result_diseaseTitle).text = it.first
            findViewById<TextView>(R.id.text_result_diseaseDetail).text = it.second
        }
    }

/*    private fun initTransitionPack() {
        val inflatingFrameLayoutAwaiting = findViewById<FrameLayout>(R.id.lout_result_frameLayout)
        val sceneProsessing = Scene.getSceneForLayout(
            inflatingFrameLayoutAwaiting,
            R.layout.activity_result_scene_processing,
            this
        )
        val sceneDone = Scene.getSceneForLayout(
            inflatingFrameLayoutAwaiting,
            R.layout.activity_result_scene_done,
            this
        )
        val transitionInBetween = TransitionInflater.from(this)
            .inflateTransition(R.transition.activity_result_scene_transition)
        transitionPack = Triple(sceneProsessing, sceneDone, transitionInBetween)
        sceneProsessing.enter()
    }*/

/*    private fun animateToDoneScene() {
        findViewById<TextView>(R.id.text_result_diseaseTitle).alpha = 1f
        findViewById<TextView>(R.id.text_result_diseaseDetail).alpha = 1f
        findViewById<ImageView>(R.id.imag_result_loading).alpha = 0f
        TransitionManager.go(transitionPack.second,transitionPack.third)//transition animation temporarily deleted
    }

    private fun animateToProcessingScene() {
        findViewById<TextView>(R.id.text_result_diseaseTitle).alpha = 0f
        findViewById<TextView>(R.id.text_result_diseaseDetail).alpha = 0f
        findViewById<ImageView>(R.id.imag_result_loading).alpha = 1f
        TransitionManager.go(transitionPack.first,transitionPack.third)//transition animation temporarily deleted
    }*/


    private fun getTransferredBitmap(): Bitmap {
        val pictureFilePath: String? = intent.getStringExtra("path")
        if (pictureFilePath == null) {
            throw RuntimeException("ACTIVITY_RESULT PICTURE FILE PATH READ FAILED")
        } else {
            return module_imageprocessing.obtainBitmapFromFilePath(pictureFilePath)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_scene_done)
/*        initTransitionPack()*/
        supportActionBar?.hide()
        val scaledBitmap: Bitmap = getTransferredBitmap()
        imageBuffer = scaledBitmap
        allViews.add(findViewById<ImageView>(R.id.imag_result_imageView).also {
            it.setImageBitmap(scaledBitmap)
        })
        allViews.add(findViewById<TextView>(R.id.text_result_diseaseTitle).also {
            it.typeface = Typeface.createFromAsset(assets, "lora_font.ttf")
            it.text = "Calculating..."
        }
        )
        allViews.add(findViewById<TextView>(R.id.text_result_diseaseDetail).also {
            it.typeface = Typeface.createFromAsset(assets, "lora_font.ttf")
            it.text = "Good things coming soon!"
        })
        getResultIndexFromBitmap(scaledBitmap).let {

            displayDisease(it)
        }


        // Load the model file into torch model
/*        try {
            pytorchModule = module_pytorch(this, "big_model.ptl")
        } catch (e: IOException) {
            Log.e("TORCH", "Cannot found pth file from assets folder :(")
            finish()
        }*/
/*        Thread() {
            inferAndDisplay()
        }.run()*/
    }

/*    override fun onResume() {
        super.onResume()
        displayDisease("DF")
        Handler().postDelayed(Runnable { animateToDoneScene() }, 500)
    }*/

}