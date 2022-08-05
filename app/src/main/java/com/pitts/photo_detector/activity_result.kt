package com.pitts.photo_detector

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Scene
import androidx.transition.Transition

class activity_result : AppCompatActivity() {
    private var allViews: MutableSet<View> = mutableSetOf()
    private var imageBuffer: Bitmap? = null
    private lateinit var pytorchModule: module_pytorch
    private val indexDict: HashMap<String, Pair<Int, Int>> = hashMapOf(
        "LOAD" to Pair(R.string.disease_loading_title, R.string.disease_loading_detail),
        "ERR" to Pair(R.string.disease_error, R.string.disease_error),
        "DF" to Pair(R.string.disease_df_title, R.string.disease_df_detail),
        "BCC" to Pair(R.string.disease_bcc_title, R.string.disease_bcc_detail),
        "MEL" to Pair(R.string.disease_mel_title, R.string.disease_mel_detail),
        "SCC" to Pair(R.string.disease_scc_title, R.string.disease_scc_detail),
        "NV" to Pair(R.string.disease_nv_title, R.string.disease_nv_detail)
    )
    private lateinit var transitionPack: Triple<Scene, Scene, Transition>


    private fun getResultStringFromBitmap(bitmap: Bitmap): String {
        val resultOfInference = module_pytorch.runInference(bitmap, this)
        Log.d("ACTIVITY_RESULT", resultOfInference.toString())
/*        animateToDoneScene()*/
        return resultOfInference.toString()
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
    private fun displayDisease(diseaseStringKey: String) {
        val returnedStringLocation: Pair<Int, Int>? = indexDict[diseaseStringKey]
        findViewById<TextView>(R.id.text_result_diseaseTitle).text =
            getString(returnedStringLocation?.first ?: R.string.disease_error)
        findViewById<TextView>(R.id.text_result_diseaseDetail).text =
            getString(returnedStringLocation?.second ?: R.string.disease_error)
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
        val picturePath = intent.getStringExtra("path")
        Log.e("debug", picturePath.toString())
        val bm = BitmapFactory.decodeFile(picturePath.toString())
        return Bitmap.createScaledBitmap(bm, 200, 200, true)
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
            it.text = getString(R.string.disease_loading_title)
        }
        )
        allViews.add(findViewById<TextView>(R.id.text_result_diseaseDetail).also {
            it.typeface = Typeface.createFromAsset(assets, "lora_font.ttf")
            it.text = getString(R.string.disease_loading_detail)
        })
        val inferedDiseaseString = getResultStringFromBitmap(scaledBitmap)
        displayDisease(inferedDiseaseString)

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