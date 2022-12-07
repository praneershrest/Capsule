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

/**
 * Fragment for user to manually create and log their outfit
 */
class OutfitsFragment : Fragment(), HorizontalRecyclerViewAdapter.OnClothingSelectedListener {

    private lateinit var clothingCategoryStrList : Array<String>

    private lateinit var listView : ListView
    private lateinit var logOutfitButton : Button
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var clothingHistoryList : ArrayList<ClothingHistory>
    private lateinit var outfitsListViewAdapter : OutfitsListViewAdapter
    private lateinit var outfitsViewModel: OutfitsViewModel

    /**
     * List view adapter where each item in the list is a horizontally scrollable list where
     * the user can select the clothing in that specific category
     * @param clothing: ArrayList where each item is a list of clothing belonging to a specific category
     */
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

        // Create the horizontal recycler view inside the list
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val v : View = View.inflate(requireActivity(), R.layout.outfits_row_item, null)

            val textView : TextView = v.findViewById(R.id.outfitRowTitleTextView)
            val recyclerView : RecyclerView = v.findViewById(R.id.outfitRowRecyclerView)

            // set text for certain category of clothing
            textView.text = clothingCategoryStrList[p0]

            // set the horizontal recycler view adapter
            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = HorizontalRecyclerViewAdapter(this@OutfitsFragment, clothingCategoryStrList[p0], clothing[p0], this@OutfitsFragment)

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

    // Initialize the ArrayList of List of clothing in each category
    private fun initializeLists() {
        clothingCategoryStrList = resources.getStringArray(R.array.category_items)
        clothingHistoryList = ArrayList()
        for (i in 1..clothingCategoryStrList.size) {
            clothingHistoryList.add(ClothingHistory(clothingId = -1L))
        }
    }

    // Create the button and add functionality to the
    private fun initializeButton(v : View) {
        logOutfitButton = v.findViewById(R.id.manuallyLogOutfitButton)
        logOutfitButton.setOnClickListener {

            val timeInMillis = Calendar.getInstance().timeInMillis
            var flag = false

            // set time while also checking if each item is selected none
            // flag is flipped when at least one item selected item is not none
            for (clothing in clothingHistoryList) {
                flag = flag || (clothing.clothingId != -1L)
                clothing.date = timeInMillis
            }

            // if all of the items are selected as none make Toast and cancel submission
            if (!flag) {
                Toast.makeText(requireActivity(), R.string.invalid_manual_outfit_log, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // insert the outfits, record date of last submitted outfit and make toast
            outfitsViewModel.insertOutfit(clothingHistoryList)
            findNavController().navigate(R.id.action_navigation_outfits_manual_to_navigation_outfits_history)
            with(sharedPreferences.edit()) {
                putLong(getString(R.string.has_inserted_for_day_key), timeInMillis)
                apply()
            }
            Toast.makeText(requireActivity(), R.string.outfit_logged, Toast.LENGTH_SHORT).show()
        }
    }

    // create listview rows of clothing belonging to each category using adapter defined inside this
    // fragment
    private fun initializeListView(v : View) {
        listView = v.findViewById(R.id.outfitListView)
        val emptyArrayList : ArrayList<List<Clothing>> = arrayListOf()
        for (i in clothingCategoryStrList.indices) {
            emptyArrayList.add(listOf())
        }

        outfitsListViewAdapter = OutfitsListViewAdapter(emptyArrayList)
        listView.adapter = outfitsListViewAdapter
    }

    // create the ViewModel where it watches if the closet has been updated, where it will then update
    // each list view respectively
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

    // function implemented for HorizontalRecyclerViewAdapter.OnClothingSelectedListener
    // records which outfits have been selected
    override fun onClothingSelected(clothingCategory: String, clothingId: Long) {
        val index = clothingCategoryStrList.indexOf(clothingCategory)
        clothingHistoryList.get(index).clothingId = clothingId
    }

}