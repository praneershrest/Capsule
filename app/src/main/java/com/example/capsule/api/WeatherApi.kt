package com.example.capsule.api

import android.location.Location
import com.example.capsule.utils.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import kotlin.math.roundToInt


class WeatherApi{
    private var temp = 0

    private lateinit var result: String
    private lateinit var season: String
    private lateinit var weather: String

    suspend fun getWeatherTemp(location: Location, apiKey: String) : ArrayList<String>{
        val lat = location.latitude
        val long = location.longitude
        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$long&appid="+apiKey
//        println("DEBUG -> capsule  $url")
        val job = CoroutineScope(Dispatchers.Default).launch{
                parseJson(url)
                setSeason()
        }
        job.join()
        val list = ArrayList<String>()
        list.add(season)
        list.add(weather)
        list.add(temp.toString())
        return list
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
        temp = (main.getDouble("temp") - 273.15).roundToInt()
        val weatherArray = json.get("weather") as JSONArray
        val weatherJson = weatherArray.getJSONObject(0)
        weather = weatherJson.getString("main")
    }


    private fun setSeason(){
        season = Util.determineSeason(temp)
    }

}