package com.example.capsule.api

import android.location.Location
import com.example.capsule.utils.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL


class WeatherApi{
    private var temp = 0.0

    private lateinit var result: String
    private lateinit var season: String

    suspend fun getWeatherTemp(location: Location) : String {
        val lat = location.latitude
        val long = location.longitude
        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$long&appid=500de63fee376381d50c5e71803fc4d7"
        val job = CoroutineScope(Dispatchers.Default).launch{
                parseJson(url)
                setSeason()
        }
        job.join()
        return season
    }

    private suspend fun parseJson(url: String){
        val job = CoroutineScope(Dispatchers.Default).launch{
            try{
                result = URL(url).readText()
            }
            catch (e : Exception){
                println("capsule-> cannot get result $e")
            }
        }
        job.join()
        val json = JSONObject(result)
        val main  = json.get("main") as JSONObject
        temp = main.getDouble("temp")
    }


    private fun setSeason(){
        season = Util.determineSeason(temp - 273.15)
    }

}