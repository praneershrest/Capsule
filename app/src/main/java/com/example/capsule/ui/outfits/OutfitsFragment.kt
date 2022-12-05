package com.example.capsule.ui.outfits

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capsule.R
import com.example.capsule.database.ClothingDatabase
import com.example.capsule.database.Repository
import com.example.capsule.model.Clothing
import com.example.capsule.model.ClothingHistory
import java.util.Calendar

class OutfitsFragment : Fragment(), HorizontalRecyclerViewAdapter.OnClothingSelectedListener {

    private lateinit var clothingCategoryStrList : Array<String>

    private lateinit var listView : ListView
    private lateinit var logOutfitButton : Button
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var clothingHistoryList : ArrayList<ClothingHistory>
    private lateinit var outfitsListViewAdapter : OutfitsListViewAdapter
    private lateinit var outfitsViewModel: OutfitsViewModel

    inner class OutfitsListViewAdapter(private var clothing : ArrayList<List<Clothing>>) : BaseAdapter() {

        override fun getCount(): Int {
            return clothingCategoryStrList.size
        }

        override fun getItem(p0: Int): Any {
            return clothingCategoryStrList[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val v : View = View.inflate(requireActivity(), R.layout.outfits_row_item, null)

            val textView : TextView = v.findViewById(R.id.outfitRowTitleTextView)
            val recyclerView : RecyclerView = v.findViewById(R.id.outfitRowRecyclerView)

            textView.text = clothingCategoryStrList[p0]

            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = HorizontalRecyclerViewAdapter(clothingCategoryStrList[p0], clothing[p0], this@OutfitsFragment)

            return v
        }

        fun replace(row: Int, clothing : List<Clothing>) {
            this.clothing[row] = clothing
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_outfits, null)
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        initializeLists()
        initializeListView(v)
        initializeOutfitsViewModel()
        initializeButton(v)

        return v
    }

    private fun initializeLists() {
        clothingCategoryStrList = resources.getStringArray(R.array.category_items)
        clothingHistoryList = ArrayList()
        for (i in 1..clothingCategoryStrList.size) {
            clothingHistoryList.add(ClothingHistory(clothingId = -1L))
        }
    }

    private fun initializeButton(v : View) {
        logOutfitButton = v.findViewById(R.id.manuallyLogOutfitButton)
        logOutfitButton.setOnClickListener {
            // put button function here to view model
            val timeInMillis = Calendar.getInstance().timeInMillis
            for (clothing in clothingHistoryList) {
                clothing.date = timeInMillis
            }
            outfitsViewModel.insertOutfit(clothingHistoryList)
            findNavController().navigate(R.id.action_navigation_outfits_manual_to_navigation_outfits_history)
            with(sharedPreferences.edit()) {
                putLong(getString(R.string.has_inserted_for_day_key), timeInMillis)
                apply()
            }
            Toast.makeText(requireActivity(), R.string.outfit_logged, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializeListView(v : View) {
        listView = v.findViewById(R.id.outfitListView)
        val emptyArrayList : ArrayList<List<Clothing>> = arrayListOf()
        for (i in clothingCategoryStrList.indices) {
            emptyArrayList.add(listOf())
        }

        outfitsListViewAdapter = OutfitsListViewAdapter(emptyArrayList)
        listView.adapter = outfitsListViewAdapter
    }

    private fun initializeOutfitsViewModel() {
        val clothingDatabase = ClothingDatabase.getInstance(requireActivity())
        val repository = Repository(clothingDatabase.clothingDatabaseDao, clothingDatabase.clothingHistoryDatabaseDao)
        outfitsViewModel = ViewModelProvider(requireActivity(), OutfitsViewModelFactory(repository))[OutfitsViewModel::class.java]

        for ((i, allClothingInCategory) in outfitsViewModel.allClothingHistoryCategoryList.withIndex()) {
            allClothingInCategory.observe(requireActivity()) {
                outfitsListViewAdapter.replace(i, it)
                outfitsListViewAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onClothingSelected(clothingCategory: String, clothingId: Long) {
        val index = clothingCategoryStrList.indexOf(clothingCategory)
        clothingHistoryList.get(index).clothingId = clothingId
    }

}