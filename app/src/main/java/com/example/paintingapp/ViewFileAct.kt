package com.example.paintingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class ViewFileAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_file)
        var intent = intent
        if (intent != null){
            var imageView = findViewById<ImageView>(R.id.image)
            imageView.setImageURI(intent.data)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}