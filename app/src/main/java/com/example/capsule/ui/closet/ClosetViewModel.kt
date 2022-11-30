package com.example.capsule.ui.closet

import androidx.lifecycle.*
import com.example.capsule.database.Repository
import java.lang.IllegalArgumentException

class ClosetViewModel(private val repository: Repository) : ViewModel() {
    private val _allClothingEntriesLiveData = repository.allClothing.asLiveData()
    private val _topsFrequenciesLiveData = repository.topsFrequencies.asLiveData()
    private val _bottomsFrequenciesLiveData = repository.bottomsFrequencies.asLiveData()
    private val _outerwearFrequenciesLiveData = repository.outerwearFrequencies.asLiveData()
    private val _shoesFrequenciesLiveData = repository.shoesFrequencies.asLiveData()

    val allClothingEntriesLiveData = _allClothingEntriesLiveData
    val topsFrequenciesLiveData = _topsFrequenciesLiveData
    val bottomsFrequenciesLiveData = _bottomsFrequenciesLiveData
    val outerwearFrequenciesLiveData = _outerwearFrequenciesLiveData
    val shoesFrequenciesLiveData = _shoesFrequenciesLiveData
}

class ClosetViewModelFactory (private val repository: Repository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ //create() creates a new instance of the modelClass, which is CommentViewModel in this case.
        if(modelClass.isAssignableFrom(ClosetViewModel::class.java))
            return ClosetViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}