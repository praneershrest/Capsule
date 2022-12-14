package com.example.capsule.ui.outfitSuggestion

import android.Manifest
import android.content.Context
import android.content.pm.ApplicationInfo
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
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.capsule.R
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

/**
 * Fragment which shows an outfit suggestion for the day based on frequency of wear and the current season taken from
 * the openweather API
 */
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
    private lateinit var progressBar : ProgressBar

    private lateinit var outfitSuggestionTopLayout: LinearLayout
    private lateinit var outfitSuggestionBottomLayout: LinearLayout
    private lateinit var emptyStateLinearLayout: LinearLayout

    private lateinit var calendar: Calendar

    private lateinit var weatherApi: WeatherApi
    private lateinit var locationManager: LocationManager
    private lateinit var season:String
    private lateinit var weatherImageView: ImageView
    private lateinit var tempTextView: TextView
    private lateinit var apiKey: String

    private var topFlag = false
    private var bottomFlag = false
    private var outerWearFlag = false
    private var shoesFlag = false

    private var _binding: FragmentOutfitSuggestionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    /**
     * on create view to check permissions to get a photo form gallery and initialise all of the public values
     */
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
        progressBar = root.findViewById(R.id.progress_bar)

        outfitSuggestionTopLayout = root.findViewById(R.id.outfit_suggestion_top_layout)
        outfitSuggestionBottomLayout = root.findViewById(R.id.outfit_suggestion_bottom_layout)
        emptyStateLinearLayout = root.findViewById(R.id.outfit_suggestion_empty_state)

        tempTextView.text = getString(R.string.creating_outfit)
        weatherImageView.visibility = View.GONE

        weatherApi = WeatherApi()
        initLocationManager()
        initOutfitSuggestion()

        val ai: ApplicationInfo = requireActivity().applicationContext.packageManager
            .getApplicationInfo(requireActivity().applicationContext.packageName, PackageManager.GET_META_DATA)
        apiKey = ai.metaData["apiKey"].toString()
        return root
    }

    /**
     * gets SharedPreferences to get last submission date and compare to today where if it is the same
     * the user will instead see outfits history
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val currentDay = Calendar.getInstance()
        val lastSubmissionDay = Calendar.getInstance()

        lastSubmissionDay.timeInMillis = sharedPreferences.getLong(getString(R.string.has_inserted_for_day_key), 0L)

        if (currentDay.get(Calendar.DAY_OF_YEAR) == lastSubmissionDay.get(Calendar.DAY_OF_YEAR) &&
            currentDay.get(Calendar.YEAR) == lastSubmissionDay.get(Calendar.YEAR)) {
            findNavController().navigate(R.id.action_navigation_outfits_suggestion_to_navigation_outfits_history)
        }

    }

    /**
     * Initialise the database and viewmodel for this activity as well as setting up observers for each of the clothing categories
     */

    private fun initOutfitSuggestion(){
        database = ClothingDatabase.getInstance(requireActivity())
        clothingDatabaseDao = database.clothingDatabaseDao
        clothingHistoryDatabaseDao = database.clothingHistoryDatabaseDao
        databaseRepository = Repository(clothingDatabaseDao, clothingHistoryDatabaseDao)
        factory = OutfitSuggestionViewModelFactory(databaseRepository)
        outfitSuggestionViewModel = ViewModelProvider(this, factory)[OutfitSuggestionViewModel::class.java]

        outfitSuggestionViewModel.suggestedTopLiveData.observe(requireActivity()) {
            if(it != null) {
                topFlag = true
                suggestedTop = it
                suggestedTopImageView.setImageURI(it.img_uri.toUri())
            } else {
                topFlag = false
            }
            displayOutfitSuggestion()
        }

        outfitSuggestionViewModel.suggestedBottomLiveData.observe(requireActivity()) {
            if(it != null) {
                bottomFlag = true
                suggestedBottom = it
                suggestedBottomImageView.setImageURI(it.img_uri.toUri())
            } else {
                bottomFlag = false
            }
            displayOutfitSuggestion()
        }

        outfitSuggestionViewModel.suggestedOuterwearLiveData.observe(requireActivity()) {
            if(it != null) {
                outerWearFlag = true
                suggestedOuterwear = it
                suggestedOuterwearImageView.setImageURI(it.img_uri.toUri())
            } else {
                outerWearFlag = false
            }
            displayOutfitSuggestion()
        }

        outfitSuggestionViewModel.suggestedShoesLiveData.observe(requireActivity()) {
            if(it != null) {
                shoesFlag = true
                suggestedShoes = it
                suggestedShoesImageView.setImageURI(it.img_uri.toUri())
            } else {
                shoesFlag = false
            }
            displayOutfitSuggestion()
        }

        outfitSuggestionViewModel.season.observe(requireActivity()){
            season = it
        }

        outfitSuggestionViewModel.weather.observe(requireActivity()){
            getWeatherIcon(it)
        }

        outfitSuggestionViewModel.temp.observe(requireActivity()){
            val tempText = it.toString() + "??C"
            tempTextView.text = tempText
            progressBar.visibility = View.GONE
        }


        logSuggestedOutfitButton.setOnClickListener {
            var clothingInserted = false
            if(topFlag && ::suggestedTop.isInitialized) {
                val entry = ClothingHistory(
                    clothingId = suggestedTop.id,
                    date = calendar.timeInMillis,
                    isSuggested = true
                )
                outfitSuggestionViewModel.insert(entry)
                clothingInserted = true
            }
            if(bottomFlag && ::suggestedBottom.isInitialized) {
                val entry = ClothingHistory(
                    clothingId = suggestedBottom.id,
                    date = calendar.timeInMillis,
                    isSuggested = true
                )
                outfitSuggestionViewModel.insert(entry)
                clothingInserted = true
            }
            if(outerWearFlag && ::suggestedOuterwear.isInitialized) {
                val entry = ClothingHistory(
                    clothingId = suggestedOuterwear.id,
                    date = calendar.timeInMillis,
                    isSuggested = true
                )
                outfitSuggestionViewModel.insert(entry)
                clothingInserted = true
            }
            if(shoesFlag && ::suggestedShoes.isInitialized) {
                val entry = ClothingHistory(
                    clothingId = suggestedShoes.id,
                    date = calendar.timeInMillis,
                    isSuggested = true
                )
                outfitSuggestionViewModel.insert(entry)
                clothingInserted = true
            }
            if (clothingInserted) {
                findNavController().navigate(R.id.action_navigation_outfits_suggestion_to_navigation_outfits_history)
                with(requireActivity().getPreferences(Context.MODE_PRIVATE).edit()) {
                    putLong(getString(R.string.has_inserted_for_day_key), calendar.timeInMillis)
                    apply()
                }
                Toast.makeText(requireActivity(), R.string.outfit_logged, Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(requireActivity(), R.string.empty_outfit_log, Toast.LENGTH_SHORT).show()
            }
        }

        // TODO handle moving to OutfitFragment
        logManualOutfitButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_outfits_suggestion_to_navigation_outfits_manual)
        }
    }

    /**
     * initialise location manager and request for location updates with this activity as the listener
     */
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

    /**
     * On location changed, trigger the updateSeason method in the view model to get the current season and get
     * the clothing suggestions. Once the function is done, stop the request for location updates to prevent unnecessary overhead
     */
    override fun onLocationChanged(location: Location) {
        outfitSuggestionViewModel.updateSeason(location, weatherApi, apiKey)
        locationManager.removeUpdates(this)
    }

    /**
     * Check and request permission for getting location if permission was not provided by the user
     */

    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
    }

    /**
     * Set the corresponding weather icon in the weatherImageView
     */
    private fun getWeatherIcon(string: String) {
        weatherImageView.visibility = View.VISIBLE
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


    /**
     * Hides the empty state layouts when the view is populated and initialised
     */
    private fun removeEmptyState() {
        emptyStateLinearLayout.visibility = View.GONE
        outfitSuggestionTopLayout.visibility = View.VISIBLE
        outfitSuggestionBottomLayout.visibility = View.VISIBLE
        logSuggestedOutfitButton.visibility = View.VISIBLE
    }

    /**
     * Hides the empty state layouts when the view is unpopulated and uninitialised
     */
    private fun displayEmptyState() {
        outfitSuggestionTopLayout.visibility = View.GONE
        outfitSuggestionBottomLayout.visibility = View.GONE
        logSuggestedOutfitButton.visibility = View.GONE
        emptyStateLinearLayout.visibility = View.VISIBLE
    }

    /**
     * check to see if outfitsuggestion is initialised and populated to call displayEmptyState or removeEmptyState
     */
    private fun displayOutfitSuggestion() {
        if (topFlag && bottomFlag && outerWearFlag && shoesFlag
                && ::suggestedTop.isInitialized
                && ::suggestedBottom.isInitialized
                && ::suggestedShoes.isInitialized
                && ::suggestedOuterwear.isInitialized) {
            removeEmptyState()
        } else {
            displayEmptyState()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}