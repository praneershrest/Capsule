package com.example.capsule.ui.welcome

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.capsule.R

class Welcome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

    }

    fun onUploadClosetClicked(view: View) {
        println("capsule-> onUploadClosetClicked")
        intent.putExtra(getString(R.string.first_time_user), false)
        setResult(Activity.RESULT_FIRST_USER, intent)
        finish()
    }
}