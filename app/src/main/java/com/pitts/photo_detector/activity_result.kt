package com.pitts.photo_detector

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class activity_result : AppCompatActivity() {
    private var allViews: MutableSet<View> = mutableSetOf()
    private var imageBuffer: Bitmap? = null
    private var resultIndex: Int = -1
    private lateinit var pytorchModule: module_pytorch
    private var indexDict: HashMap<Int, Pair<Int, Int>> = hashMapOf(
        -1 to Pair(R.string.disease_loading_title, R.string.disease_loading_detail),
        0 to Pair(R.string.disease_error, R.string.disease_error),
        1 to Pair(R.string.disease_eczema_title, R.string.disease_eczema_detail),
        2 to Pair(R.string.disease_rosacea_title, R.string.disease_rosacea_detail)
    )

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        supportActionBar?.hide()


        val picturePath = intent.getStringExtra("path")
        Log.e("debug", picturePath.toString())
        val bm = BitmapFactory.decodeFile(picturePath.toString())
        val createScaledBitmap = Bitmap.createScaledBitmap(bm, 200, 200, true)
        allViews.add(findViewById<ImageView>(R.id.imag_result_imageView).also {
            it.setImageBitmap(createScaledBitmap)
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
        imageBuffer = bm
        // Load the model file into torch model
        try {
            pytorchModule = module_pytorch(this, "big_model.ptl")
        } catch (e: IOException) {
            Log.e("TORCH", "Cannot found pth file from assets folder :(")
            finish()
        }
        Thread(){
            inferAndDisplay()
        }.run()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}