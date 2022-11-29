package com.example.capsule.ui.outfitSuggestion

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.capsule.R
import com.example.capsule.Util
import com.example.capsule.database.ClothingDatabase
import com.example.capsule.database.ClothingDatabaseDao
import com.example.capsule.database.ClothingHistoryDatabaseDao
import com.example.capsule.database.Repository
import com.example.capsule.databinding.FragmentOutfitSuggestionsBinding
import com.example.capsule.model.Clothing
import com.example.capsule.ui.stats.ItemWearFrequency
import kotlin.collections.HashMap

class OutfitSuggestionFragment: Fragment() {
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var database: ClothingDatabase
    private lateinit var clothingDatabaseDao: ClothingDatabaseDao
    private lateinit var clothingHistoryDatabaseDao: ClothingHistoryDatabaseDao
    private lateinit var databaseRepository: Repository
    private lateinit var factory: OutfitSuggestionViewModelFactory
    private lateinit var outfitsViewModel: OutfitSuggestionViewModel

    private lateinit var suggestedTopImageView: ImageView
    private lateinit var suggestedBottomImageView: ImageView
    private lateinit var suggestedOuterwearImageView: ImageView
    private lateinit var suggestedShoesImageView: ImageView
    private lateinit var logSuggestedOutfitButton: Button
    private lateinit var logManualOutfitButton: Button

    private var _binding: FragmentOutfitSuggestionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOutfitSuggestionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        suggestedTopImageView = root.findViewById(R.id.suggested_top_iv)
        suggestedBottomImageView = root.findViewById(R.id.suggested_bottom_iv)
        suggestedOuterwearImageView = root.findViewById(R.id.suggested_outerwear_iv)
        suggestedShoesImageView = root.findViewById(R.id.suggested_shoes_iv)
        logSuggestedOutfitButton = root.findViewById(R.id.log_suggested_outfit_btn)
        logManualOutfitButton = root.findViewById(R.id.log_manual_outfit_btn)

        val season = getSeason()

        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE)

        database = ClothingDatabase.getInstance(requireActivity())
        clothingDatabaseDao = database.clothingDatabaseDao
        clothingHistoryDatabaseDao = database.clothingHistoryDatabaseDao
        databaseRepository = Repository(clothingDatabaseDao, clothingHistoryDatabaseDao)
        factory = OutfitSuggestionViewModelFactory(databaseRepository, season)
        outfitsViewModel = ViewModelProvider(this, factory)[OutfitSuggestionViewModel::class.java]

        /*
            ViewModels for each category is triggered initially with changes to season
            The suggested clothing for each category is calculated and set to MutableLiveData
            The MutableLiveData is tracked and changes to it trigger the images in the view to be changed
            The suggested cloth's URI is saved in SharedPreferences so that the suggested clothing can be displayed when returning to fragment
        */
        outfitsViewModel.topsForSeasonLiveData.observe(requireActivity()) { tops ->
            outfitsViewModel.topsFrequencies.observe(requireActivity()) { topsFreq ->
                outfitsViewModel.suggestedTop.value = getSuggestedClothingId(tops, topsFreq)
                with(sharedPreferences.edit()) {
                    putString(getString(R.string.suggested_top_uri), outfitsViewModel.suggestedTop.value?.img_uri)
                    apply()
                }
            }
        }

        outfitsViewModel.bottomsForSeasonLiveData.observe(requireActivity()) { bottoms ->
            outfitsViewModel.bottomsFrequencies.observe(requireActivity()) { bottomsFreq ->
                outfitsViewModel.suggestedBottom.value = getSuggestedClothingId(bottoms, bottomsFreq)
                with(sharedPreferences.edit()) {
                    putString(getString(R.string.suggested_bottom_uri), outfitsViewModel.suggestedBottom.value?.img_uri)
                    apply()
                }
            }
        }

        outfitsViewModel.outerwearForSeasonLiveData.observe(requireActivity()) { outerwear ->
            outfitsViewModel.outerwearFrequencies.observe(requireActivity()) { outerwearFreq ->
                outfitsViewModel.suggestedOuterwear.value = getSuggestedClothingId(outerwear, outerwearFreq)
                with(sharedPreferences.edit()) {
                    putString(getString(R.string.suggested_outerwear_uri), outfitsViewModel.suggestedOuterwear.value?.img_uri)
                    apply()
                }
            }
        }

        outfitsViewModel.shoesForSeasonLiveData.observe(requireActivity()) { shoes ->
            outfitsViewModel.shoesFrequencies.observe(requireActivity()) { shoesFreq ->
                outfitsViewModel.suggestedShoes.value = getSuggestedClothingId(shoes, shoesFreq)
                with(sharedPreferences.edit()) {
                    putString(getString(R.string.suggested_shoes_uri), outfitsViewModel.suggestedShoes.value?.img_uri)
                    apply()
                }
            }
        }

        /*
            When the value of the suggested clothing (for particular category) changes the image is updated
        */
        outfitsViewModel.suggestedTop.observe(requireActivity()) {
            if(it != null) {
                suggestedTopImageView.setImageBitmap(Util.getBitmap(requireActivity(), Uri.parse(it.img_uri)))
            }
        }
        outfitsViewModel.suggestedBottom.observe(requireActivity()) {
            if(it != null) {
                suggestedBottomImageView.setImageBitmap(Util.getBitmap(requireActivity(), Uri.parse(it.img_uri)))
            }
        }
        outfitsViewModel.suggestedOuterwear.observe(requireActivity()) {
            if(it != null) {
                suggestedOuterwearImageView.setImageBitmap(Util.getBitmap(requireActivity(), Uri.parse(it.img_uri)))
            }
        }
        outfitsViewModel.suggestedShoes.observe(requireActivity()) {
            if(it != null) {
                suggestedShoesImageView.setImageBitmap(Util.getBitmap(requireActivity(), Uri.parse(it.img_uri)))
            }
        }

        // TODO handle inserting suggested outfit to clothing_history_table
        logSuggestedOutfitButton.setOnClickListener {
            println("LOG SUGGESTED OUTFIT CLICKED")
        }

        // TODO handle moving to OutfitFragment
        logManualOutfitButton.setOnClickListener {
            println("LOG MANUAL OUTFIT CLICKED")
        }

        /*
            Check if there is a suggested outfit and display it
            When season changes, the ViewModel will be triggered and the SharedPreference values will be updated with the new suggested items
        */
        val suggestedTopUriString = sharedPreferences.getString(getString(R.string.suggested_top_uri), "EMPTY")
        if(suggestedTopUriString?.isNotEmpty() == true) {
            suggestedTopImageView.setImageBitmap(Util.getBitmap(requireActivity(), suggestedTopUriString.toUri()))
        }

        val suggestedBottomUriString = sharedPreferences.getString(getString(R.string.suggested_bottom_uri), "")
        if(suggestedBottomUriString?.isNotEmpty() == true) {
            suggestedBottomImageView.setImageBitmap(Util.getBitmap(requireActivity(), suggestedBottomUriString.toUri()))
        }

        val suggestedOuterwearUriString = sharedPreferences.getString(getString(R.string.suggested_outerwear_uri), "")
        if(suggestedOuterwearUriString?.isNotEmpty() == true) {
            suggestedOuterwearImageView.setImageBitmap(Util.getBitmap(requireActivity(), suggestedOuterwearUriString.toUri()))
        }

        val suggestedShoesUriString = sharedPreferences.getString(getString(R.string.suggested_shoes_uri), "")
        if(suggestedShoesUriString?.isNotEmpty() == true) {
            suggestedShoesImageView.setImageBitmap(Util.getBitmap(requireActivity(), suggestedShoesUriString.toUri()))
        }

        return root
    }

    // TODO get season from api
    private fun getSeason() : String {
        return "Winter"
    }

    private fun initFreqMap(clothingList: List<Clothing>) : HashMap<Long, Int> {
        val map: HashMap<Long, Int> = HashMap()
        clothingList.forEach {
            map[it.id] = 0
        }
        return map
    }

    private fun getSuggestedClothingId(clothingList: List<Clothing>, frequencyList: List<ItemWearFrequency>) : Clothing? {
        var suggestedId: Long = 0
        var minFrequency: Int = Int.MAX_VALUE
        val frequencyMap = initFreqMap(clothingList)
        frequencyList.forEach {
            if(frequencyMap.containsKey(it.clothing_id)) {
                frequencyMap[it.clothing_id] = it.frequency
            }
        }
        frequencyMap.forEach {
            if(it.value < minFrequency) {
                minFrequency = it.value
                suggestedId = it.key
            }
        }
        clothingList.forEach {
            if(it.id == suggestedId) {
                return it
            }
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}