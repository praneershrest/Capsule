package com.example.capsule.ui.welcome

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.capsule.MainActivity
import com.example.capsule.R

class Welcome : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        sharedPreferences = this.getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE)
    }

    fun onUploadClosetClicked(view: View) {
        with(sharedPreferences.edit()) {
            putBoolean(getString(R.string.first_time_user), false)
            apply()
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}