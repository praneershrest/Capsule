package com.example.capsule.ui.outfits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capsule.R
import com.example.capsule.database.ClothingDatabase
import com.example.capsule.database.Repository
import com.example.capsule.model.Clothing
import com.example.capsule.model.ClothingHistory

class OutfitsFragment : Fragment(), HorizontalRecyclerViewAdapter.OnClothingSelectedListener {

    private lateinit var clothingCategoryStrList : Array<String>

    private lateinit var listView : ListView
    private lateinit var logOutfitButton : Button

    private lateinit var clothingHistory: ClothingHistory
    private lateinit var outfitsListViewAdapter : OutfitsListViewAdapter
    private lateinit var outfitsViewModel: OutfitsViewModel

    companion object {
        private val TITLES : List<String> = listOf("Tops", "Bottoms", "Jackets", "Shoes")
    }

    // TODO edit adapter to use List of clothing instead of adapter
    inner class OutfitsListViewAdapter(private var clothing : List<Clothing>) : BaseAdapter() {

        // just placeholder values A List<List<Clothing>> instead
        // to get the actual images
        private val stuff = listOf(
            listOf(R.drawable.ic_outline_shopping_bag_24, R.drawable.ic_outline_shopping_bag_24, R.drawable.ic_outline_shopping_bag_24, R.drawable.ic_outline_shopping_bag_24)
            , listOf(R.drawable.ic_notifications_black_24dp, R.drawable.ic_notifications_black_24dp, R.drawable.ic_notifications_black_24dp, R.drawable.ic_notifications_black_24dp)
            , listOf(R.drawable.ic_home_black_24dp, R.drawable.ic_home_black_24dp, R.drawable.ic_home_black_24dp, R.drawable.ic_home_black_24dp, R.drawable.ic_home_black_24dp, R.drawable.ic_home_black_24dp)
            , listOf(R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground)
        )

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
            recyclerView.adapter = HorizontalRecyclerViewAdapter(p0, stuff[p0], this@OutfitsFragment)

            return v
        }

        fun replace(clothing : List<Clothing>) {
            this.clothing = clothing
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_outfits, null)

        clothingCategoryStrList = resources.getStringArray(R.array.category_items)
        initializeListView(v)
        initializeButton(v)
        initializeOutfitsViewModel()

        return v
    }

    // TODO add function to add clothing history to database
    private fun initializeButton(v : View) {
        logOutfitButton = v.findViewById(R.id.manuallyLogOutfitButton)
        logOutfitButton.setOnClickListener {
            // put button function here to view model
        }
    }

    private fun initializeListView(v : View) {
        listView = v.findViewById(R.id.outfitListView)
        outfitsListViewAdapter = OutfitsListViewAdapter(ArrayList<Clothing>())
        listView.adapter = outfitsListViewAdapter
    }

    private fun initializeOutfitsViewModel() {
        val clothingDatabase = ClothingDatabase.getInstance(requireActivity())
        val repository = Repository(clothingDatabase.clothingDatabaseDao, clothingDatabase.clothingHistoryDatabaseDao)
        outfitsViewModel = ViewModelProvider(requireActivity(), OutfitsViewModelFactory(repository))[OutfitsViewModel::class.java]
        outfitsViewModel.allClothingLiveData.observe(requireActivity()) {
            outfitsListViewAdapter.replace(it)
            outfitsListViewAdapter.notifyDataSetChanged()
        }
    }

    // TODO edit function so that it modifies the ClothingHistory field
    override fun onClothingSelected(clothingCategory: Int, position: Int) {
        println("CLOTHING CATEGORY: ${clothingCategoryStrList[clothingCategory]}")
    }

}