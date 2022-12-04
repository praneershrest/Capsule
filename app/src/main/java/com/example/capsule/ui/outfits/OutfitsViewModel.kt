package com.example.capsule.ui.outfits

import androidx.lifecycle.*
import com.example.capsule.database.Repository
import com.example.capsule.model.Clothing
import com.example.capsule.model.ClothingHistory

// TODO add function to add clothing history to database
//  and also change allClothingLiveData into List<List<Clothing>> where the row will
//  represent each category of clothing
class OutfitsViewModel(private val repository: Repository) : ViewModel() {

    private val allClothingTops : LiveData<List<Clothing>> = repository.getAllClothingInCategory("Tops").asLiveData()
    private val allClothingOuterwear : LiveData<List<Clothing>> = repository.getAllClothingInCategory("Outerwear").asLiveData()
    private val allClothingPants : LiveData<List<Clothing>> = repository.getAllClothingInCategory("Pants").asLiveData()
    private val allClothingShoes : LiveData<List<Clothing>> = repository.getAllClothingInCategory("Shoes").asLiveData()

    val allClothingHistoryCategoryList = listOf(allClothingTops, allClothingOuterwear, allClothingPants, allClothingShoes)

    fun insertOutfit(clothingHistory: List<ClothingHistory>) {
        for (ch in clothingHistory) {
            if (ch.id != -1L) {
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