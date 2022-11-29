package com.example.capsule.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.capsule.R
import com.example.capsule.database.ClothingDatabase
import com.example.capsule.database.ClothingDatabaseDao
import com.example.capsule.database.ClothingHistoryDatabaseDao
import com.example.capsule.database.Repository

class FrequencyStatsFragment: Fragment() {
    private lateinit var statsViewModel: StatsViewModel
    private lateinit var database: ClothingDatabase
    private lateinit var clothingDatabaseDao: ClothingDatabaseDao
    private lateinit var historyDatabaseDao: ClothingHistoryDatabaseDao
    private lateinit var repository: Repository
    private lateinit var factory: StatsViewModelFactory

    private lateinit var freqListView: ListView
    private lateinit var frequencyList: List<ItemWearFrequency>
    private lateinit var adapter: FrequencyListAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_frequency_stats, container, false)

        database = ClothingDatabase.getInstance(requireActivity())
        clothingDatabaseDao = database.clothingDatabaseDao
        historyDatabaseDao = database.clothingHistoryDatabaseDao
        repository = Repository(clothingDatabaseDao, historyDatabaseDao)
        factory = StatsViewModelFactory(repository)

        freqListView = view.findViewById(R.id.freq_listview)
        frequencyList = ArrayList()
        adapter = FrequencyListAdapter(requireActivity(), frequencyList)
        freqListView.adapter = adapter

        statsViewModel = ViewModelProvider(requireActivity(), factory).get(StatsViewModel::class.java)
        statsViewModel.topsFrequencies.observe(requireActivity()) {
            println("DEBUG top freq: $it")
            frequencyList = it
            adapter.replace(it)
            adapter.notifyDataSetChanged()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}