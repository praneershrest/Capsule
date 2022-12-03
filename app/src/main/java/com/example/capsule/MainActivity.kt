package com.example.capsule

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.capsule.databinding.ActivityMainBinding
import com.example.capsule.ui.welcome.Welcome

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {  }
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = this.getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_outfits, R.id.navigation_closet, R.id.navigation_stats, R.id.navigation_tips
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if(sharedPreferences.getBoolean(getString(R.string.first_time_user), true)) {
            navView.visibility = View.INVISIBLE
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
        }
        else {
            navView.visibility = View.VISIBLE
        }
    }
}