package com.example.capsule.api

import android.location.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL


class WeatherApi{
    private var temp: Double = 0.0

    private var precip: Double = 0.0

    fun getWeather(location: Location){
        val lat = location.latitude
        val long = location.longitude
        CoroutineScope(Dispatchers.IO).launch{
            val url = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+long+"&appid="+"500de63fee376381d50c5e71803fc4d7"
            val result = URL(url).readText()
            val json = JSONObject(result)
            val main  = json.get("main") as JSONObject
            val rain = json.get("rain") as JSONObject
            temp = main.getDouble("temp")
            precip = rain.getDouble("1h")
        }
    }

    fun getTemp() : Double{
        return temp
    }

    fun getPrecip(): Double{
        return precip
    }







}