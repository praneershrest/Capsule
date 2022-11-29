package com.example.capsule.api

import android.location.Location
import com.example.capsule.ui.outfits.OutfitsViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject
import java.net.URL


class WeatherApi{
    private var temp = 0.0

    private lateinit var outfitsViewModel: OutfitsViewModel

    fun getWeatherTemp(location: Location, viewModel: OutfitsViewModel){
        val lat = location.latitude
        val long = location.longitude
        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$long&appid=500de63fee376381d50c5e71803fc4d7"
        outfitsViewModel = viewModel
        CoroutineScope(Dispatchers.Default).launch{
            parseJson(url)
            setTemp()
        }
    }

    private fun parseJson(url: String){
        val result = URL(url).readText()
        val json = JSONObject(result)
        val main  = json.get("main") as JSONObject
        temp = main.getDouble("temp")
    }

    private suspend fun setTemp(){
        withContext(Main){
            outfitsViewModel.temp.value = temp
        }

    }









}