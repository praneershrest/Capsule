package com.example.capsule.ui.closet

import androidx.lifecycle.*
import com.example.capsule.database.Repository
import com.example.capsule.model.Clothing
import java.lang.IllegalArgumentException

/**
 * Stores the tops, bottoms, outwear, shoes and has all database commands
 * @param repository: stores the main apps respository for database access
 */
class ClosetViewModel(private val repository: Repository) : ViewModel() {
    private val _allClothingEntriesLiveData = repository.allClothing.asLiveData()
    private val _topsFrequenciesLiveData = repository.topsAllFrequencies.asLiveData()
    private val _bottomsFrequenciesLiveData = repository.bottomsAllFrequencies.asLiveData()
    private val _outerwearFrequenciesLiveData = repository.outerwearAllFrequencies.asLiveData()
    private val _shoesFrequenciesLiveData = repository.shoesAllFrequencies.asLiveData()

    val allClothingEntriesLiveData = _allClothingEntriesLiveData
    val topsFrequenciesLiveData = _topsFrequenciesLiveData
    val bottomsFrequenciesLiveData = _bottomsFrequenciesLiveData
    val outerwearFrequenciesLiveData = _outerwearFrequenciesLiveData
    val shoesFrequenciesLiveData = _shoesFrequenciesLiveData


    fun remove(clothingId: Long) {
        repository.deleteClothing(clothingId)
    }
}

class ClosetViewModelFactory (private val repository: Repository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ //create() creates a new instance of the modelClass, which is CommentViewModel in this case.
        if(modelClass.isAssignableFrom(ClosetViewModel::class.java))
            return ClosetViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}