package com.example.capsule.ui.outfitSuggestion

import androidx.lifecycle.*
import com.example.capsule.database.Repository
import com.example.capsule.model.Clothing
import java.lang.IllegalArgumentException

class OutfitSuggestionViewModel(repository: Repository, season: String) : ViewModel() {
    private val _topsForSeasonLiveData = repository.clothesByCategoryForSeason("Tops", season).asLiveData()
    private val _bottomsForSeasonLiveData = repository.clothesByCategoryForSeason("Bottoms", season).asLiveData()
    private val _outerwearForSeasonLiveData = repository.clothesByCategoryForSeason("Outerwear", season).asLiveData()
    private val _shoesForSeasonLiveData = repository.clothesByCategoryForSeason("Shoes", season).asLiveData()

    private val _topsFrequencies = repository.topsFrequencies.asLiveData()
    private val _bottomsFrequencies = repository.bottomsFrequencies.asLiveData()
    private val _outerwearFrequencies = repository.outerwearFrequencies.asLiveData()
    private val _shoesFrequencies = repository.shoesFrequencies.asLiveData()

    val topsForSeasonLiveData = _topsForSeasonLiveData
    val bottomsForSeasonLiveData =_bottomsForSeasonLiveData
    val outerwearForSeasonLiveData = _outerwearForSeasonLiveData
    val shoesForSeasonLiveData = _shoesForSeasonLiveData

    val topsFrequencies = _topsFrequencies
    val bottomsFrequencies = _bottomsFrequencies
    val outerwearFrequencies = _outerwearFrequencies
    val shoesFrequencies = _shoesFrequencies

    val suggestedTop = MutableLiveData<Clothing>()
    val suggestedBottom = MutableLiveData<Clothing>()
    val suggestedOuterwear = MutableLiveData<Clothing>()
    val suggestedShoes = MutableLiveData<Clothing>()
}

class OutfitSuggestionViewModelFactory (private val repository: Repository, private val season: String) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ //create() creates a new instance of the modelClass
        if(modelClass.isAssignableFrom(OutfitSuggestionViewModel::class.java))
            return OutfitSuggestionViewModel(repository, season) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}