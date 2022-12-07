package com.example.capsule.ui.stats

import androidx.lifecycle.*
import com.example.capsule.database.Repository
import java.lang.IllegalArgumentException

// ViewModel for retrieving statistics from the database
class StatsViewModel(private val repository: Repository) : ViewModel() {
    val topsFrequencies = repository.topsFrequencies.asLiveData()
    val bottomsFrequencies = repository.bottomsFrequencies.asLiveData()
    val shoesFrequencies = repository.shoesFrequencies.asLiveData()
    val outerwearFrequencies = repository.outerwearFrequencies.asLiveData()

    val materialFrequencies = repository.materialFrequencies.asLiveData()
    val purchaseLocationFrequencies = repository.purchaseLocationFrequencies.asLiveData()

    private val _text = MutableLiveData<String>().apply {
        value = "This is stats Fragment"
    }
    val text: LiveData<String> = _text
}

class StatsViewModelFactory (private val repository: Repository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(StatsViewModel::class.java))
            return StatsViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}