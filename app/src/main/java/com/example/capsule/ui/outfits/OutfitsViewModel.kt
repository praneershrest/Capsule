package com.example.capsule.ui.outfits

import androidx.lifecycle.*
import com.example.capsule.database.Repository
import com.example.capsule.model.Clothing

// TODO add function to add clothing history to database
//  and also change allClothingLiveData into List<List<Clothing>> where the row will
//  represent each category of clothing
class OutfitsViewModel(private val repository: Repository) : ViewModel() {

    val allClothingLiveData : LiveData<List<Clothing>> = repository.allClothing.asLiveData()

}

class OutfitsViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OutfitsViewModel::class.java)) {
            return OutfitsViewModel(repository) as T
        }
        throw IllegalArgumentException("Passed in modelClass not OutfitsViewModel")
    }

}