package com.example.capsule.ui.outfits

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task

class OutfitsFragment : Fragment() {

    private var _binding: FragmentOutfitsBinding? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var location: Location
    private lateinit var weatherApi: WeatherApi
    private var temp: Double = 0.0
    private var precip: Double = 0.0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val outfitsViewModel =
            ViewModelProvider(this).get(OutfitsViewModel::class.java)

        _binding = FragmentOutfitsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        outfitsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        checkPermission()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        println("DEBUG season: " + Util.determineSeason(temp))
        return root
    }

    private fun getLastLocation(){
        checkPermission()
        val locationTask : Task<Location> = fusedLocationProviderClient.lastLocation

        locationTask.addOnSuccessListener { OnSuccessListener<Location> {
            location = it
            getWeather()
        }}
    }

    private fun getWeather(){
        weatherApi = WeatherApi()
        weatherApi.getWeather(location)
        temp = weatherApi.getTemp()
        precip = weatherApi.getPrecip()
    }


    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(requireActivity(), arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION), 0)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        requireActivity().onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}