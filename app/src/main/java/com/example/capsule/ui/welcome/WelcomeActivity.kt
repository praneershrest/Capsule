package com.example.capsule.ui.welcome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.capsule.R

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
    }

    fun onUploadClosetClicked(view: View) {
        finish()
    }
}