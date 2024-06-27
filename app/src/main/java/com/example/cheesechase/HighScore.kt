package com.example.cheesechase

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HighScore : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_high_score)
        val next = findViewById<ImageButton>(R.id.next)
        next.setOnClickListener {
            val intent = Intent(this,FrontPage::class.java)
            startActivity(intent)
        }
    }
    override fun onBackPressed() {
        val intent = Intent(applicationContext, FrontPage::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}