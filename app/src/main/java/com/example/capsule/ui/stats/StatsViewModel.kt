package com.example.capsule.ui.stats

import androidx.lifecycle.*
import com.example.capsule.database.Repository
import java.lang.IllegalArgumentException

class StatsViewModel(private val repository: Repository) : ViewModel() {
    val topsFrequencies = repository.topsFrequencies.asLiveData()

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