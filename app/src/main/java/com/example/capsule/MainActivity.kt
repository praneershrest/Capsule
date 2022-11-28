package com.example.capsule

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.capsule.database.ClothingDatabase
import com.example.capsule.database.ClothingDatabaseDao
import com.example.capsule.database.ClothingHistoryDatabaseDao
import com.example.capsule.database.Repository
import com.example.capsule.databinding.ActivityMainBinding
import com.example.capsule.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    private lateinit var navController: NavController

    private lateinit var welcomeActivityIntent: Intent

    private lateinit var database: ClothingDatabase
    private lateinit var clothingDatabaseDao: ClothingDatabaseDao
    private lateinit var clothingHistoryDatabaseDao: ClothingHistoryDatabaseDao
    private lateinit var databaseRepository: Repository
    private lateinit var factory: MainActivityViewModelFactory
    private lateinit var mainActivityViewModel: MainActivityViewModel

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {  }
        super.onCreate(savedInstanceState)

        sharedPreferences = this.getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE)
        database = ClothingDatabase.getInstance(this)
        clothingDatabaseDao = database.clothingDatabaseDao
        clothingHistoryDatabaseDao = database.clothingHistoryDatabaseDao
        databaseRepository = Repository(clothingDatabaseDao, clothingHistoryDatabaseDao)
        factory = MainActivityViewModelFactory(databaseRepository)
        mainActivityViewModel = ViewModelProvider(this, factory)[MainActivityViewModel::class.java]

        mainActivityViewModel.clothingTableSizeLiveData.observe(this) {
            // Show welcome screen if database is empty
            if(it == 0) {
                with(sharedPreferences.edit()) {
                    putBoolean(getString(R.string.empty_database), true)
                    apply()
                }
                welcomeActivityIntent = Intent(this, WelcomeActivity::class.java)
                startActivity(welcomeActivityIntent)
            }
            else {
                with(sharedPreferences.edit()) {
                    putBoolean(getString(R.string.empty_database), false)
                    apply()
                }
            }
        }

        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_outfits, R.id.navigation_closet, R.id.navigation_stats, R.id.navigation_tips
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.visibility = View.GONE

        // If the database is empty, navigate to ClosetFragment with hidden navView
        // Else un-hide navView and continue normal workflow
        if(sharedPreferences.getBoolean(getString(R.string.empty_database), true)) {
            navController.navigate(R.id.navigation_closet)
        }
        else {
            navView.visibility = View.VISIBLE
        }
    }
}