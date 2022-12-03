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
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.runBlocking

class OutfitSuggestionViewModel(private val repository: Repository) : ViewModel(){

    private val _season = MutableLiveData<String>()
    val season : LiveData<String>
    get(){
        return _season
    }

    private val _suggestedTopLiveData = MutableLiveData<Clothing>()
    val suggestedTopLiveData = _suggestedTopLiveData

    private val _suggestedBottomLiveData = MutableLiveData<Clothing>()
    val suggestedBottomLiveData = _suggestedBottomLiveData

    private val _suggestedOuterwearLiveData =  MutableLiveData<Clothing>()
    val suggestedOuterwearLiveData = _suggestedOuterwearLiveData

    private val _suggestedShoesLiveData =  MutableLiveData<Clothing>()
    val suggestedShoesLiveData = _suggestedShoesLiveData

    fun insert(clothingHistoryEntry: ClothingHistory) {
        repository.insertClothingHistory(clothingHistoryEntry)
    }

    fun updateSeason(location: Location, weatherApi: WeatherApi){
        var chosenSeason: String
        runBlocking {
            chosenSeason = weatherApi.getWeatherTemp(location)
            _season.value = chosenSeason
            repository.suggestedClothingByCategoryForSeason("Tops", chosenSeason).takeWhile { it.season == chosenSeason }.take(1).collect {
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



class OutfitSuggestionViewModelFactory (private val repository: Repository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ // create() creates a new instance of the modelClass
        if(modelClass.isAssignableFrom(OutfitSuggestionViewModel::class.java))
            return OutfitSuggestionViewModel(repository) as T
        throw IllegalArgumentException("DEBUG Unknown ViewModel class")
    }
}