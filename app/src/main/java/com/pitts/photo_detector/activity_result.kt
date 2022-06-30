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

class activity_result : AppCompatActivity() {
    private var allViews: MutableSet<View> = mutableSetOf()
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
            it.text = when (intent.getIntExtra("DISEASE_INDEX", 0)) {
                0 -> getString(R.string.disease_eczema_title)
                1 -> getString(R.string.disease_psoriasis_title)
                2 -> getString(R.string.disease_rosacea_title)
                else -> getString(R.string.disease_error)
            }
        }
        )
        allViews.add(findViewById<TextView>(R.id.text_result_diseaseDetail).also {

            it.text = when (intent.getIntExtra("DISEASE_INDEX", 0)) {
                0 -> getString(R.string.disease_eczema_detail)
                1 -> getString(R.string.disease_psoriasis_detail)
                2 -> getString(R.string.disease_rosacea_detail)
                else -> getString(R.string.disease_error)
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}