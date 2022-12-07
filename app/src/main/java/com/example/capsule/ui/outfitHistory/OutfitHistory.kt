package com.example.capsule.ui.outfitHistory

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
import java.util.Calendar

/**
 * Fragment that displays the most recent outfits worn in the past 7 days
 */
class OutfitHistory : Fragment() {
    private lateinit var database: ClothingDatabase
    private lateinit var clothingDatabaseDao: ClothingDatabaseDao
    private lateinit var historyDatabaseDao: ClothingHistoryDatabaseDao
    private lateinit var repository: Repository
    private lateinit var factory: OutfitHistoryViewModelFactory
    private lateinit var viewModel: OutfitHistoryViewModel

    private lateinit var listView: ListView
    private lateinit var adapter: OutfitHistoryListAdapter
    private lateinit var recentClothingList: List<RecentClothing>

    private lateinit var calendar: Calendar
    private var startDate: Long = 0L
    private var endDate: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outfit_history, container, false)

        calendar = Calendar.getInstance()
        initDates()

        // initialize repository and viewmodel
        database = ClothingDatabase.getInstance(requireActivity())
        clothingDatabaseDao = database.clothingDatabaseDao
        historyDatabaseDao = database.clothingHistoryDatabaseDao
        repository = Repository(clothingDatabaseDao, historyDatabaseDao)
        factory = OutfitHistoryViewModelFactory(repository, startDate, endDate)
        viewModel = ViewModelProvider(
            requireActivity(),
            factory
        )[OutfitHistoryViewModel::class.java]

        // initialize listview with adapter
        listView = view.findViewById(R.id.outfit_history_listView)
        recentClothingList = ArrayList()
        adapter = OutfitHistoryListAdapter(requireActivity(), recentClothingList)
        listView.adapter = adapter

        // add new outfit in UI if new outfit is added in database
        viewModel.clothingList.observe(requireActivity()) {
            recentClothingList = it
            adapter.replace(it)
            adapter.notifyDataSetChanged()
        }

        return view
    }

    // calculate the past week dates
    private fun initDates() {
        endDate = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR,-7)
        startDate = calendar.timeInMillis
    }

}