package com.example.capsule.api

import android.location.Location
import com.example.capsule.Util
import com.example.capsule.database.Repository
import com.example.capsule.ui.outfitSuggestion.OutfitSuggestionViewModelFactory
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL


class WeatherApi{
    private var temp = 0.0

    private lateinit var result: String
    private lateinit var season: String

    private lateinit var databaseRepository: Repository

    private lateinit var outfitSuggestionViewModelFactory: OutfitSuggestionViewModelFactory

    suspend fun getWeatherTemp(location: Location, repo: Repository) : OutfitSuggestionViewModelFactory{
        println("DEBUG WEATHER TEMP CALLED")
        val lat = location.latitude
        val long = location.longitude
        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$long&appid=500de63fee376381d50c5e71803fc4d7"
        databaseRepository = repo
        val job = CoroutineScope(Dispatchers.Default).launch{
                println("DEBUG IN COROUTINE WEATHER")
                parseJson(url)
                outfitSuggestionViewModelFactory = setSeasonAndGetFactory()
        }
        job.join()
        println("DEBUG BEFORE OUTFIT SUGGESTION RETURN")
        return outfitSuggestionViewModelFactory
    }

    private suspend fun parseJson(url: String){
        println("DEBUG IN PARSE JSON")
        val job = CoroutineScope(Dispatchers.Default).launch{
            result = URL(url).readText()
        }
//        val result = "{\"coord\":{\"lon\":-122.9274,\"lat\":49.2811},\"weather\":[{\"id\":601,\"main\":\"Snow\",\"description\":\"snow\",\"icon\":\"13n\"}],\"base\":\"stations\",\"main\":{\"temp\":271.62,\"feels_like\":268.13,\"temp_min\":270.55,\"temp_max\":274.15,\"pressure\":995,\"humidity\":96},\"visibility\":145,\"wind\":{\"speed\":2.68,\"deg\":107,\"gust\":6.26},\"snow\":{\"1h\":1.27},\"clouds\":{\"all\":100},\"dt\":1669802142,\"sys\":{\"type\":2,\"id\":2004473,\"country\":\"CA\",\"sunrise\":1669823075,\"sunset\":1669853802},\"timezone\":-28800,\"id\":5911606,\"name\":\"Burnaby\",\"cod\":200}"
        job.join()
        println("DEBUG AFTER RESULT")
        val json = JSONObject(result)
        val main  = json.get("main") as JSONObject
        temp = main.getDouble("temp")
        println("DEBUG GET RESULT WHICH IS $result")
    }

    private fun setSeasonAndGetFactory() : OutfitSuggestionViewModelFactory{
        println("DEBUG: SET SEASON CALLED")
        println("DEBUG ASYNC SEASON")
//          outfitSuggestionViewModel._season.value = Util.determineSeason(temp - 273.15)
        season = Util.determineSeason(temp - 273.15)
        println("DEBUG SEASON IS $season")
        return OutfitSuggestionViewModelFactory(databaseRepository, season)
    }
}