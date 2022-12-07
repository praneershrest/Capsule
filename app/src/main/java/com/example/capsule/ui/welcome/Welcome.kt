package com.example.capsule.ui.welcome

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.capsule.R

/**
 * Activity that is only launched when the user has launched for the first time
 */
class Welcome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

    }

    // Mark that it's not user's first time anymore and send result to parent activity
    fun onUploadClosetClicked(view: View) {
        intent.putExtra(getString(R.string.first_time_user), false)
        setResult(Activity.RESULT_FIRST_USER, intent)
        finish()
    }
}