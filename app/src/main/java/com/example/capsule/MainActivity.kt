package com.example.capsule

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.capsule.databinding.ActivityMainBinding
import com.example.capsule.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var welcomeActivityIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {  }
        super.onCreate(savedInstanceState)

        // TODO: Check with sharedPref flag if welcomPage should display or not
        var showWelcome = true
        if(showWelcome) {
            println("Capsule-debug: in onCreate()")
            welcomeActivityIntent = Intent(this, WelcomeActivity::class.java)
            startActivity(welcomeActivityIntent)
        }

        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }
}