package com.example.capsule.ui.outfitSuggestion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.capsule.R
import com.example.capsule.Util
import com.example.capsule.database.ClothingDatabase
import com.example.capsule.database.ClothingDatabaseDao
import com.example.capsule.database.ClothingHistoryDatabaseDao
import com.example.capsule.database.Repository
import com.example.capsule.databinding.FragmentOutfitSuggestionsBinding
import com.example.capsule.model.Clothing
import com.example.capsule.model.ClothingHistory
import java.util.Calendar

class OutfitSuggestionFragment: Fragment() {

    private lateinit var database: ClothingDatabase
    private lateinit var clothingDatabaseDao: ClothingDatabaseDao
    private lateinit var clothingHistoryDatabaseDao: ClothingHistoryDatabaseDao
    private lateinit var databaseRepository: Repository
    private lateinit var factory: OutfitSuggestionViewModelFactory
    private lateinit var outfitSuggestionViewModel: OutfitSuggestionViewModel

    private lateinit var suggestedTop: Clothing
    private lateinit var suggestedBottom: Clothing
    private lateinit var suggestedOuterwear: Clothing
    private lateinit var suggestedShoes: Clothing

    private lateinit var suggestedTopImageView: ImageView
    private lateinit var suggestedBottomImageView: ImageView
    private lateinit var suggestedOuterwearImageView: ImageView
    private lateinit var suggestedShoesImageView: ImageView
    private lateinit var logSuggestedOutfitButton: Button
    private lateinit var logManualOutfitButton: Button

    private lateinit var calendar: Calendar

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
        calendar = Calendar.getInstance()

        database = ClothingDatabase.getInstance(requireActivity())
        clothingDatabaseDao = database.clothingDatabaseDao
        clothingHistoryDatabaseDao = database.clothingHistoryDatabaseDao
        databaseRepository = Repository(clothingDatabaseDao, clothingHistoryDatabaseDao)
        factory = OutfitSuggestionViewModelFactory(databaseRepository, season)
        outfitSuggestionViewModel = ViewModelProvider(this, factory)[OutfitSuggestionViewModel::class.java]

        outfitSuggestionViewModel.suggestedTopLiveData.observe(requireActivity()) {
            if(it != null) {
                suggestedTop = it
                suggestedTopImageView.setImageBitmap(Util.getBitmap(requireActivity(), it.img_uri.toUri()))
            }
        }

        outfitSuggestionViewModel.suggestedBottomLiveData.observe(requireActivity()) {
            if(it != null) {
                suggestedBottom = it
                suggestedBottomImageView.setImageBitmap(Util.getBitmap(requireActivity(), it.img_uri.toUri()))
            }
        }

        outfitSuggestionViewModel.suggestedOuterwearLiveData.observe(requireActivity()) {
            if(it != null) {
                suggestedOuterwear = it
                suggestedOuterwearImageView.setImageBitmap(Util.getBitmap(requireActivity(), it.img_uri.toUri()))
            }
        }

        outfitSuggestionViewModel.suggestedShoesLiveData.observe(requireActivity()) {
            if(it != null) {
                suggestedShoes = it
                suggestedShoesImageView.setImageBitmap(Util.getBitmap(requireActivity(), it.img_uri.toUri()))
            }
        }

        logSuggestedOutfitButton.setOnClickListener {
            if(::suggestedTop.isInitialized) {
                val entry = ClothingHistory(
                    clothingId = suggestedTop.id,
                    date = calendar.timeInMillis,
                    isSuggested = true
                )
                outfitSuggestionViewModel.insert(entry)
            }
            if(::suggestedBottom.isInitialized) {
                val entry = ClothingHistory(
                    clothingId = suggestedBottom.id,
                    date = calendar.timeInMillis,
                    isSuggested = true
                )
                outfitSuggestionViewModel.insert(entry)
            }
            if(::suggestedOuterwear.isInitialized) {
                val entry = ClothingHistory(
                    clothingId = suggestedOuterwear.id,
                    date = calendar.timeInMillis,
                    isSuggested = true
                )
                outfitSuggestionViewModel.insert(entry)
            }
            if(::suggestedShoes.isInitialized) {
                val entry = ClothingHistory(
                    clothingId = suggestedShoes.id,
                    date = calendar.timeInMillis,
                    isSuggested = true
                )
                outfitSuggestionViewModel.insert(entry)
            }
            // TODO: change Fragment to OutfitHistory
        }

        // TODO handle moving to OutfitFragment
        logManualOutfitButton.setOnClickListener {
            println("LOG MANUAL OUTFIT CLICKED")
            findNavController().navigate(R.id.action_navigation_outfits_to_navigation_outfits_manual)
        }

        return root
    }

    // TODO get season from api
    private fun getSeason() : String {
        return "Winter"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}