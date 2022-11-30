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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class FrequencyStatsFragment: Fragment() {
    private lateinit var statsViewModel: StatsViewModel
    private lateinit var database: ClothingDatabase
    private lateinit var clothingDatabaseDao: ClothingDatabaseDao
    private lateinit var historyDatabaseDao: ClothingHistoryDatabaseDao
    private lateinit var repository: Repository
    private lateinit var factory: StatsViewModelFactory

    private lateinit var categoryList: List<String>
    private lateinit var freqTabLayout: TabLayout
    private lateinit var freqListView: ListView
    private lateinit var frequencyList: List<ItemWearFrequency>
    private lateinit var adapter: FrequencyListAdapter

    private var selectedTab: Int = 0

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

        // Set up tab layout for categories
        freqTabLayout = view.findViewById(R.id.freq_tab_layout)
        categoryList = requireActivity()?.resources?.getStringArray(R.array.category_items)!!.toList()
        for (category in categoryList) {
            freqTabLayout.addTab(freqTabLayout.newTab().setText(category))
        }

        statsViewModel = ViewModelProvider(requireActivity(), factory).get(StatsViewModel::class.java)
        statsViewModel.topsFrequencies.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Tops") {
                frequencyList = it
                adapter.replace(it)
                adapter.notifyDataSetChanged()
            }
        }
        statsViewModel.bottomsFrequencies.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Bottoms") {
                frequencyList = it
                adapter.replace(it)
                adapter.notifyDataSetChanged()
            }
        }
        statsViewModel.outerwearFrequencies.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Outerwear") {
                frequencyList = it
                adapter.replace(it)
                adapter.notifyDataSetChanged()
            }
        }
        statsViewModel.shoesFrequencies.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Shoes") {
                frequencyList = it
                adapter.replace(it)
                adapter.notifyDataSetChanged()
            }
        }

        // Add TabListener to update list when different category selected
        freqTabLayout.addOnTabSelectedListener(object: OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTab = tab!!.position
                when (categoryList[selectedTab]) {
                    "Tops" -> frequencyList = statsViewModel.topsFrequencies.value!!
                    "Bottoms" -> frequencyList = statsViewModel.bottomsFrequencies.value!!
                    "Outerwear" -> frequencyList = statsViewModel.outerwearFrequencies.value!!
                    "Shoes" -> frequencyList = statsViewModel.shoesFrequencies.value!!
                }
                adapter.replace(frequencyList)
                adapter.notifyDataSetChanged()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        return view
    }

}