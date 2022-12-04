package com.example.capsule

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.capsule.databinding.ActivityMainBinding
import com.example.capsule.ui.welcome.Welcome
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding

    companion object {
        val menuItems = arrayOf(
            CbnMenuItem(
                R.drawable.ic_home_black_24dp,
                R.drawable.avd_outfit,
                R.id.navigation_outfits
            ),
            CbnMenuItem(
                R.drawable.ic_closet_24,
                R.drawable.avd_closet,
                R.id.navigation_closet
            ),
            CbnMenuItem(
                R.drawable.ic_baseline_bar_chart_24,
                R.drawable.avd_stats,
                R.id.navigation_stats
            ),
            CbnMenuItem(
                R.drawable.ic_tips_24px,
                R.drawable.avd_tips,
                R.id.navigation_tips
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {  }
        super.onCreate(savedInstanceState)

        val activeIndex = savedInstanceState?.getInt("activeIndex") ?: 0

        supportActionBar?.hide()

        sharedPreferences = this.getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Uses CurvedBottomNavigationView which is a BottomNavigationView with styling
        // Used from open-source library: https://github.com/susonthapa/curved-bottom-navigation
        val navView: CurvedBottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_outfits, R.id.navigation_closet, R.id.navigation_stats, R.id.navigation_tips
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setMenuItems(menuItems, activeIndex)
        navView.setupWithNavController(navController)

        if(sharedPreferences.getBoolean(getString(R.string.first_time_user), true)) {
            navView.visibility = View.GONE
            resultLauncher.launch(Intent(this, Welcome::class.java))
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_FIRST_USER) {
            println("capsule-> FIRSTUSER TRACE")
            binding.navView.onMenuItemClick(1)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("activeIndex", binding.navView.getSelectedIndex())
        super.onSaveInstanceState(outState)
    }
}