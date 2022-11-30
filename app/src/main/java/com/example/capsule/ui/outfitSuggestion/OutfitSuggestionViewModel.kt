package com.example.capsule.ui.outfitSuggestion

import androidx.lifecycle.*
import com.example.capsule.database.Repository
import com.example.capsule.model.ClothingHistory
import java.lang.IllegalArgumentException

class OutfitSuggestionViewModel(private val repository: Repository, private val season: String) : ViewModel() {

    private val _suggestedTopLiveData = repository.suggestedClothingByCategoryForSeason("Tops", season).asLiveData()
    val suggestedTopLiveData = _suggestedTopLiveData

    private val _suggestedBottomLiveData = repository.suggestedClothingByCategoryForSeason("Bottoms", season).asLiveData()
    val suggestedBottomLiveData = _suggestedBottomLiveData

    private val _suggestedOuterwearLiveData = repository.suggestedClothingByCategoryForSeason("Outerwear", season).asLiveData()
    val suggestedOuterwearLiveData = _suggestedOuterwearLiveData

    private val _suggestedShoesLiveData = repository.suggestedClothingByCategoryForSeason("Shoes", season).asLiveData()
    val suggestedShoesLiveData = _suggestedShoesLiveData

    fun insert(clothingHistoryEntry: ClothingHistory) {
        repository.insertClothingHistory(clothingHistoryEntry)
    }
}

class OutfitSuggestionViewModelFactory (private val repository: Repository, private val season: String) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ //create() creates a new instance of the modelClass
        if(modelClass.isAssignableFrom(OutfitSuggestionViewModel::class.java))
            return OutfitSuggestionViewModel(repository, season) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}