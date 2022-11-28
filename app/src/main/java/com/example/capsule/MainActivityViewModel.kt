package com.example.capsule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.capsule.database.Repository
import java.lang.IllegalArgumentException

class MainActivityViewModel(repository: Repository): ViewModel() {
    private val _clothingTableSizeLiveData = repository.clothingTableSize.asLiveData()

    val clothingTableSizeLiveData = _clothingTableSizeLiveData
}

class MainActivityViewModelFactory (private val repository: Repository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ //create() creates a new instance of the modelClass
        if(modelClass.isAssignableFrom(MainActivityViewModel::class.java))
            return MainActivityViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}