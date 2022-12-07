package com.example.capsule.ui.outfitSuggestion

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.capsule.api.WeatherApi
import com.example.capsule.database.Repository
import com.example.capsule.model.Clothing
import com.example.capsule.model.ClothingHistory
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking

class OutfitSuggestionViewModel(private val repository: Repository) : ViewModel(){

    private val _season = MutableLiveData<String>()
    val season : LiveData<String>
    get(){
        return _season
    }

    private val _weather = MutableLiveData<String>()
    val weather : LiveData<String>
        get(){
            return _weather
        }

    private val _temp = MutableLiveData<String>()
    val temp : LiveData<String>
        get(){
            return _temp
        }

    private val _suggestedTopLiveData = MutableLiveData<Clothing>()
    val suggestedTopLiveData = _suggestedTopLiveData

    private val _suggestedBottomLiveData = MutableLiveData<Clothing>()
    val suggestedBottomLiveData = _suggestedBottomLiveData

    private val _suggestedOuterwearLiveData =  MutableLiveData<Clothing>()
    val suggestedOuterwearLiveData = _suggestedOuterwearLiveData

    private val _suggestedShoesLiveData =  MutableLiveData<Clothing>()
    val suggestedShoesLiveData = _suggestedShoesLiveData

    /**
     * insert a new ClothingHistoryEntry if the user decides to log the suggested clothing
     */
    fun insert(clothingHistoryEntry: ClothingHistory) {
        repository.insertClothingHistory(clothingHistoryEntry)
    }

    /**
     * updateSeason function which is called from onLocationChanged and have location, weatherApi and apikey passed in
     * to then run a blocking call to get the chosen season, weather and temperature from the WeatherApi class.
     * it will then run the repository query for each of the clothing categories and update the mutableLiveData to be the updated
     * Clothing
     */
    fun updateSeason(location: Location, weatherApi: WeatherApi, apiKey:String){
        var chosenSeason: String
        var chosenWeather: String
        var chosenTemp: String
        runBlocking {
            val list = weatherApi.getWeatherTemp(location, apiKey)
            chosenSeason = list[0]
            chosenWeather = list[1]
            chosenTemp = list[2]
            _weather.value = chosenWeather
            _season.value = chosenSeason
            _temp.value = chosenTemp
            repository.suggestedClothingByCategoryForSeason("Tops", chosenSeason).take(1).collect {
                _suggestedTopLiveData.value = it
            }
            repository.suggestedClothingByCategoryForSeason("Bottoms", chosenSeason).take(1).collect {
                _suggestedBottomLiveData.value = it
            }
            repository.suggestedClothingByCategoryForSeason("Shoes", chosenSeason).take(1).collect {
                _suggestedShoesLiveData.value = it
            }
            repository.suggestedClothingByCategoryForSeason("Outerwear", chosenSeason).take(1).collect {
                _suggestedOuterwearLiveData.value = it
            }
        }
    }
}

/**
 * ViewModelFactory for the outfitSuggestionFragment
 */
class OutfitSuggestionViewModelFactory (private val repository: Repository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ // create() creates a new instance of the modelClass
        if(modelClass.isAssignableFrom(OutfitSuggestionViewModel::class.java))
            return OutfitSuggestionViewModel(repository) as T
        throw IllegalArgumentException("DEBUG Unknown ViewModel class")
    }
}