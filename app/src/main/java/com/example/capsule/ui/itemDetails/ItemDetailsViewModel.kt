package com.example.capsule.ui.itemDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.capsule.database.Repository
import com.example.capsule.model.Clothing
import java.lang.IllegalArgumentException

/**
 * Stores the insert command to insert entires into database
 * @param repository: stores the main apps respository for database access
 */
class ItemDetailsViewModel(private val repository: Repository): ViewModel() {
    private val _allClothingEntriesLiveData = repository.allClothing.asLiveData()

    fun insert(exerciseEntry: Clothing) {
        repository.insertClothing(exerciseEntry)
    }

    val allClothingEntriesLiveData = _allClothingEntriesLiveData
}

class ItemDetailsViewModelFactory (private val repository: Repository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ //create() creates a new instance of the modelClass
        if(modelClass.isAssignableFrom(ItemDetailsViewModel::class.java))
            return ItemDetailsViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}