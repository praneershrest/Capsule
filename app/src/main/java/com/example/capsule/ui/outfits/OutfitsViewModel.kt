package com.example.capsule.ui.outfits

import androidx.lifecycle.*
import com.example.capsule.database.Repository
import com.example.capsule.model.Clothing
import com.example.capsule.model.ClothingHistory

/**
 * View model that checks if any new clothes are inserted into the repository.
 */
class OutfitsViewModel(private val repository: Repository) : ViewModel() {

    private val allClothingTops : LiveData<List<Clothing>> = repository.getAllClothingInCategory("Tops").asLiveData()
    private val allClothingOuterwear : LiveData<List<Clothing>> = repository.getAllClothingInCategory("Outerwear").asLiveData()
    private val allClothingPants : LiveData<List<Clothing>> = repository.getAllClothingInCategory("Bottoms").asLiveData()
    private val allClothingShoes : LiveData<List<Clothing>> = repository.getAllClothingInCategory("Shoes").asLiveData()

    val allClothingHistoryCategoryList = listOf(allClothingTops, allClothingPants, allClothingOuterwear, allClothingShoes)

    // Check if each clothingHistory is has a valid clothingId, otherwise don't insert it
    fun insertOutfit(clothingHistory: List<ClothingHistory>) {
        for (ch in clothingHistory) {
            if (ch.clothingId != -1L) {
                repository.insertClothingHistory(ch)
            }
        }
    }

}

class OutfitsViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OutfitsViewModel::class.java)) {
            return OutfitsViewModel(repository) as T
        }
        throw IllegalArgumentException("Passed in modelClass not OutfitsViewModel")
    }

}