package com.example.capsule.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
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

    private lateinit var emptyStateIcon: ImageView
    private lateinit var emptyStateHeader: TextView
    private lateinit var emptyStateDescription: TextView

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

        emptyStateIcon = view.findViewById(R.id.freq_empty_icon)
        emptyStateHeader = view.findViewById(R.id.freq_stats_empty_state)
        emptyStateDescription = view.findViewById(R.id.freq_stats_empty_description)

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
                removeEmptyState()
                if (frequencyList.isNotEmpty()) {
                    adapter.replace(it)
                    freqListView.adapter = adapter                } else {
                    displayEmptyState(R.drawable.tshirt)
                }
            }
        }
        statsViewModel.bottomsFrequencies.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Bottoms") {
                frequencyList = it
                removeEmptyState()
                if (frequencyList.isNotEmpty()) {
                    adapter.replace(it)
                    freqListView.adapter = adapter
                } else {
                    displayEmptyState(R.drawable.trousers)
                }
            }
        }
        statsViewModel.outerwearFrequencies.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Outerwear") {
                frequencyList = it
                removeEmptyState()
                if (frequencyList.isNotEmpty()) {
                    adapter.replace(it)
                    freqListView.adapter = adapter
                } else {
                    displayEmptyState(R.drawable.coat)
                }
            }
        }
        statsViewModel.shoesFrequencies.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Shoes") {
                frequencyList = it
                removeEmptyState()
                if (frequencyList.isNotEmpty()) {
                    adapter.replace(it)
                    freqListView.adapter = adapter
                } else {
                    displayEmptyState(R.drawable.sandal)
                }
            }
        }

        // Add TabListener to update list when different category selected
        freqTabLayout.addOnTabSelectedListener(object: OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var categoryImg = R.drawable.tshirt
                selectedTab = tab!!.position
                when (categoryList[selectedTab]) {
                    "Tops" -> {
                        frequencyList = statsViewModel.topsFrequencies.value!!
                    }
                    "Bottoms" -> {
                        frequencyList = statsViewModel.bottomsFrequencies.value!!
                        categoryImg = R.drawable.trousers
                    }
                    "Outerwear" -> {
                        frequencyList = statsViewModel.outerwearFrequencies.value!!
                        categoryImg = R.drawable.coat
                    }
                    "Shoes" -> {
                        frequencyList = statsViewModel.shoesFrequencies.value!!
                        categoryImg = R.drawable.sandal
                    }
                }
                removeEmptyState()
                if (frequencyList.isNotEmpty()) {
                    adapter.replace(frequencyList)
                    adapter.notifyDataSetChanged()
                } else {
                    displayEmptyState(categoryImg)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        return view
    }

    private fun displayEmptyState(image: Int) {
        freqListView.visibility = View.GONE
        emptyStateIcon.setImageResource(image)
        emptyStateIcon.visibility = View.VISIBLE
        emptyStateHeader.visibility = View.VISIBLE
        emptyStateDescription.visibility = View.VISIBLE
    }

    private fun removeEmptyState() {
        emptyStateIcon.visibility = View.GONE
        emptyStateHeader.visibility = View.GONE
        emptyStateDescription.visibility = View.GONE
        freqListView.visibility = View.VISIBLE
    }

}