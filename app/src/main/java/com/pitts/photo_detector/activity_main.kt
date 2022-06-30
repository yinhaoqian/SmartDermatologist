package com.pitts.photo_detector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class activity_main : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<ImageView>(R.id.main_camera).setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
        findViewById<ImageView>(R.id.main_python).setOnClickListener {
            startActivity(Intent(this, PythonActivity::class.java))
        }
    }


}