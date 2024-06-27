package com.example.cheesechase

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class FrontPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_front_page)
        var f = 0
        val play = findViewById<Button>(R.id.play)
        play.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom).create()
            val view = layoutInflater.inflate(R.layout.infinite, null)
            builder.setView(view)
            builder.setCanceledOnTouchOutside(false)
            builder.setCancelable(false)
            val finite = view.findViewById<Button>(R.id.finite)
            finite.setOnClickListener {
                val animationZoomIn = AnimationUtils.loadAnimation(this, R.anim.scale_up)
                finite.startAnimation(animationZoomIn)
                val animationZoomOut = AnimationUtils.loadAnimation(this, R.anim.scale_down)
                finite.startAnimation(animationZoomOut)
                f = 1
                val intent = Intent(this, MainActivity2::class.java)
                intent.putExtra("message_key1", f)
                startActivity(intent)
            }
            val infinite = view.findViewById<Button>(R.id.infinite)
            infinite.setOnClickListener {
                val animationZoomIn = AnimationUtils.loadAnimation(this, R.anim.scale_up)
                infinite.startAnimation(animationZoomIn)
                val animationZoomOut = AnimationUtils.loadAnimation(this, R.anim.scale_down)
                infinite.startAnimation(animationZoomOut)
                f = 0
                val intent = Intent(this, MainActivity2::class.java)
                intent.putExtra("message_key1", f)
                startActivity(intent)
            }
            builder.show()
        }
    }
        override fun onBackPressed() {
            finishAffinity()
            super.onBackPressed()
        }
    }