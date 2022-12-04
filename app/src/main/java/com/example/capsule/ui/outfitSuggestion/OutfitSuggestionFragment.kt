package com.example.capsule.ui.outfitSuggestion

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.capsule.R
import com.example.capsule.utils.Util
import com.example.capsule.utils.Util.Weather
import com.example.capsule.api.WeatherApi
import com.example.capsule.database.ClothingDatabase
import com.example.capsule.database.ClothingDatabaseDao
import com.example.capsule.database.ClothingHistoryDatabaseDao
import com.example.capsule.database.Repository
import com.example.capsule.databinding.FragmentOutfitSuggestionsBinding
import com.example.capsule.model.Clothing
import com.example.capsule.model.ClothingHistory
import java.util.*

class OutfitSuggestionFragment: Fragment(), LocationListener {

    private lateinit var database: ClothingDatabase
    private lateinit var clothingDatabaseDao: ClothingDatabaseDao
    private lateinit var clothingHistoryDatabaseDao: ClothingHistoryDatabaseDao
    private lateinit var databaseRepository: Repository
    private lateinit var factory: OutfitSuggestionViewModelFactory
    private lateinit var outfitSuggestionViewModel: OutfitSuggestionViewModel

    private lateinit var suggestedTop: Clothing
    private lateinit var suggestedBottom: Clothing
    private lateinit var suggestedOuterwear: Clothing
    private lateinit var suggestedShoes: Clothing

    private lateinit var suggestedTopImageView: ImageView
    private lateinit var suggestedBottomImageView: ImageView
    private lateinit var suggestedOuterwearImageView: ImageView
    private lateinit var suggestedShoesImageView: ImageView
    private lateinit var logSuggestedOutfitButton: Button
    private lateinit var logManualOutfitButton: Button

    private lateinit var calendar: Calendar

    private lateinit var weatherApi: WeatherApi
    private lateinit var locationManager: LocationManager
    private lateinit var season:String
    private lateinit var weatherImageView: ImageView
    private lateinit var tempTextView: TextView

    private var _binding: FragmentOutfitSuggestionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOutfitSuggestionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        checkPermission()
        calendar = Calendar.getInstance()
        suggestedTopImageView = root.findViewById(R.id.suggested_top_iv)
        suggestedBottomImageView = root.findViewById(R.id.suggested_bottom_iv)
        suggestedOuterwearImageView = root.findViewById(R.id.suggested_outerwear_iv)
        suggestedShoesImageView = root.findViewById(R.id.suggested_shoes_iv)
        logSuggestedOutfitButton = root.findViewById(R.id.log_suggested_outfit_btn)
        logManualOutfitButton = root.findViewById(R.id.log_manual_outfit_btn)
        weatherImageView = root.findViewById(R.id.weather_iv)
        tempTextView = root.findViewById(R.id.temperature_tv)


        weatherApi = WeatherApi()
        initLocationManager()
        initOutfitSuggestion()
        return root
    }

    private fun initOutfitSuggestion(){
        database = ClothingDatabase.getInstance(requireActivity())
        clothingDatabaseDao = database.clothingDatabaseDao
        clothingHistoryDatabaseDao = database.clothingHistoryDatabaseDao
        databaseRepository = Repository(clothingDatabaseDao, clothingHistoryDatabaseDao)
        factory = OutfitSuggestionViewModelFactory(databaseRepository)
        outfitSuggestionViewModel = ViewModelProvider(this, factory)[OutfitSuggestionViewModel::class.java]

        outfitSuggestionViewModel.suggestedTopLiveData.observe(requireActivity()) {
            println("capsule-> TOP OBSERVER $it")
            if(it != null) {
                suggestedTop = it
                suggestedTopImageView.setImageBitmap(Util.getBitmap(requireActivity(), it.img_uri.toUri()))
            }
        }

        outfitSuggestionViewModel.suggestedBottomLiveData.observe(requireActivity()) {
            println("capsule-> BOTTOM OBSERVER $it")
            if(it != null) {
                suggestedBottom = it
                suggestedBottomImageView.setImageBitmap(Util.getBitmap(requireActivity(), it.img_uri.toUri()))
            }
        }

        outfitSuggestionViewModel.suggestedOuterwearLiveData.observe(requireActivity()) {
            println("capsule-> OUTERWEAR OBSERVER $it")
            if(it != null) {
                suggestedOuterwear = it
                suggestedOuterwearImageView.setImageBitmap(Util.getBitmap(requireActivity(), it.img_uri.toUri()))
            }
        }

        outfitSuggestionViewModel.suggestedShoesLiveData.observe(requireActivity()) {
            println("capsule-> SHOES OBSERVER $it")
            if(it != null) {
                suggestedShoes = it
                suggestedShoesImageView.setImageBitmap(Util.getBitmap(requireActivity(), it.img_uri.toUri()))
            }

        }

        outfitSuggestionViewModel.season.observe(requireActivity()){
            season = it
//            println("DEBUG in observer $season")
        }

        outfitSuggestionViewModel.weather.observe(requireActivity()){
            getWeatherIcon(it)
        }

        outfitSuggestionViewModel.temp.observe(requireActivity()){
            val tempText = it.toString() + "ºC"
            tempTextView.text = tempText
        }


        logSuggestedOutfitButton.setOnClickListener {
            if(::suggestedTop.isInitialized) {
                val entry = ClothingHistory(
                    clothingId = suggestedTop.id,
                    date = calendar.timeInMillis,
                    isSuggested = true
                )
                outfitSuggestionViewModel.insert(entry)
            }
            if(::suggestedBottom.isInitialized) {
                val entry = ClothingHistory(
                    clothingId = suggestedBottom.id,
                    date = calendar.timeInMillis,
                    isSuggested = true
                )
                outfitSuggestionViewModel.insert(entry)
            }
            if(::suggestedOuterwear.isInitialized) {
                val entry = ClothingHistory(
                    clothingId = suggestedOuterwear.id,
                    date = calendar.timeInMillis,
                    isSuggested = true
                )
                outfitSuggestionViewModel.insert(entry)
            }
            if(::suggestedShoes.isInitialized) {
                val entry = ClothingHistory(
                    clothingId = suggestedShoes.id,
                    date = calendar.timeInMillis,
                    isSuggested = true
                )
                outfitSuggestionViewModel.insert(entry)
            }
            // TODO: change Fragment to OutfitHistory
        }

        // TODO handle moving to OutfitFragment
        logManualOutfitButton.setOnClickListener {
            println("LOG MANUAL OUTFIT CLICKED")
            findNavController().navigate(R.id.action_navigation_outfits_to_navigation_outfits_manual)
        }
    }


    private fun initLocationManager() {
        try {
            locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val provider: String? = locationManager.getBestProvider(criteria, true)
            if(provider != null) {
                locationManager.requestLocationUpdates(provider, 5000, 0f, this)
            }
        } catch (e: SecurityException) {
            println("DEBUG: location manager failed to initialise")
        }
    }

    override fun onLocationChanged(location: Location) {
        outfitSuggestionViewModel.updateSeason(location, weatherApi)
    }

    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
    }

    private fun getWeatherIcon(string: String) {
        if (string == Weather.THUNDER){
                weatherImageView.setImageResource(R.drawable.thunder_icon)
            }
        else if(string == Weather.CLOUDY) {
                weatherImageView.setImageResource(R.drawable.cloudy_icon)
            }
        else if(string == Weather.DRIZZLE) {
                weatherImageView.setImageResource(R.drawable.drizzle_icon)
            }
        else if(string == Weather.SUNNY) {
                weatherImageView.setImageResource(R.drawable.sunny_icon)
            }
        else if(string == Weather.RAIN) {
                weatherImageView.setImageResource(R.drawable.rain_icon)
            }
        else if(string == Weather.SNOW){
            weatherImageView.setImageResource(R.drawable.snow_icon)
        }
        else{
            weatherImageView.setImageResource(R.drawable.fog_icon)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}