package com.pitts.photo_detector

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class activity_main : AppCompatActivity() {
    private lateinit var bounceAnimation: Animation
    private var allViews: MutableSet<View> = mutableSetOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        module_param.initParametersByDefault(this)
        supportActionBar?.hide()
        allViews.add(findViewById(R.id.main_logo))
        allViews.add(
            findViewById<ImageButton>(R.id.butt_main_startnewsession).also {
                it.setOnClickListener {
                    (it as ImageButton).startAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.buttonpush_bounce
                        )
                    )
                    Handler().postDelayed(Runnable {
                        startActivity(Intent(this, activity_camnew::class.java))
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }, 200)
                }
            })
        allViews.add(
            findViewById<ImageButton>(R.id.butt_main_userpreference).also {
                it.setOnClickListener {
                    val animation: Animation =
                        AnimationUtils.loadAnimation(this, R.anim.buttonpush_bounce)
                    (it as ImageButton).startAnimation(animation)
                }
            })
        allViews.add(
            findViewById<ImageButton>(R.id.butt_main_aboutsoftware).also {
                it.setOnClickListener {
                    val animation: Animation =
                        AnimationUtils.loadAnimation(this, R.anim.buttonpush_bounce)
                    (it as ImageButton).startAnimation(animation)
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        allViews.forEach {
            it.animate().alpha(1.0f).setDuration(1000).start();
        }
    }

    override fun onPause() {
        super.onPause()
        allViews.forEach {
            it.animate().alpha(0.0f).setDuration(1000).start();
        }
    }

}


