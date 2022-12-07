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

/**
 *  Activity where it holds almost everything with bottom navigation view and fragments
 *  Handles BottomNavigationView and first time user experience
 */
class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding

    companion object {
        // list of CbnMenuItems that defines the icon and animation that allows for fancy
        // bottom navigation view
        val menuItems = arrayOf(
            CbnMenuItem(
                R.drawable.ic_home_black_24dp,
                R.drawable.avd_outfit,
                R.id.navigation_outfits_suggestion
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
        // splash screen when launching app for first time or after closing app
        installSplashScreen().apply {  }
        super.onCreate(savedInstanceState)

        // if the savedInstance not null, set the last tab we were on for bottom navigation
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
                R.id.navigation_outfits_suggestion, R.id.navigation_closet, R.id.navigation_stats, R.id.navigation_tips
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setMenuItems(menuItems, activeIndex)
        navView.setupWithNavController(navController)

        // check if the user is launching the app for the very first time, if it is, go to welcome activity
        if(sharedPreferences.getBoolean(getString(R.string.first_time_user), true)) {
            navView.visibility = View.GONE
            resultLauncher.launch(Intent(this, Welcome::class.java))
        }
    }

    // get result when launching welcome activity
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_FIRST_USER) {
            // automatically go to closet tab in bottom navigation view and add item since ther ewould be no items in closet
            binding.navView.onMenuItemClick(1)
        }
    }

    // save the last tab we were on with the BottomNavigation view
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("activeIndex", binding.navView.getSelectedIndex())
        super.onSaveInstanceState(outState)
    }
}