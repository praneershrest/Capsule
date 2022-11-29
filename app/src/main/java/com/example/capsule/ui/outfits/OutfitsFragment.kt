package com.example.capsule.ui.outfits

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.capsule.Util
import com.example.capsule.api.WeatherApi
import com.example.capsule.databinding.FragmentOutfitsBinding


class OutfitsFragment : Fragment(), LocationListener{

    private var _binding: FragmentOutfitsBinding? = null
    private lateinit var lastLocation: Location
    private lateinit var weatherApi: WeatherApi
    private lateinit var locationManager: LocationManager
    private lateinit var outfitsViewModel: OutfitsViewModel
    private var temp: Double = 0.0
    private lateinit var season: String

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        outfitsViewModel = ViewModelProvider(this)[OutfitsViewModel::class.java]

        _binding = FragmentOutfitsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        weatherApi = WeatherApi()
        val textView: TextView = binding.textHome
        outfitsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        outfitsViewModel.temp.observe(viewLifecycleOwner) {
            temp = it - 273.15
            season = Util.determineSeason(temp)
            println("DEBUG TEMP IS: " + temp.toString())
            println("DEBUG SEASON IS: " + season)
        }
        checkPermission()
        initLocationManager()
        return root
    }


    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(requireActivity(), arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION), 0)
    }


    private fun initLocationManager() {
        try {
            locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val provider: String? = locationManager.getBestProvider(criteria, true)
            if(provider != null) {
                val location = locationManager.getLastKnownLocation(provider)
                if (location != null)
                    onLocationChanged(location)
            }
        } catch (e: SecurityException) {
            println("location manager failed to initialise")
        }
    }

    override fun onLocationChanged(location: Location) {
        lastLocation = location
        weatherApi.getWeatherTemp(lastLocation, outfitsViewModel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}