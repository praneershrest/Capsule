package com.example.capsule.ui.outfitHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.capsule.database.Repository
import java.lang.IllegalArgumentException

class OutfitHistoryViewModel(repository: Repository, startDate: Long, endDate: Long) : ViewModel() {
    private val _clothingList = repository.getAllClothingBetweenDates(startDate, endDate).asLiveData()

    val clothingList = _clothingList
}

class OutfitHistoryViewModelFactory(private val repository: Repository, private val startDate: Long, private val endDate: Long) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(OutfitHistoryViewModel::class.java))
            return OutfitHistoryViewModel(repository, startDate, endDate) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}