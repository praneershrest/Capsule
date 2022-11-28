package com.example.capsule.ui.itemDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.capsule.database.Repository
import com.example.capsule.model.Clothing
import java.lang.IllegalArgumentException

class ItemDetailsViewModel(private val repository: Repository): ViewModel() {
    private val _allClothingEntriesLiveData = repository.allClothing.asLiveData()

    fun insert(exerciseEntry: Clothing) {
        repository.insertClothing(exerciseEntry)
    }

    val allClothingEntriesLiveData = _allClothingEntriesLiveData
}

// class modified from lecture code
class ItemDetailsViewModelFactory (private val repository: Repository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ //create() creates a new instance of the modelClass, which is CommentViewModel in this case.
        if(modelClass.isAssignableFrom(ItemDetailsViewModel::class.java))
            return ItemDetailsViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}