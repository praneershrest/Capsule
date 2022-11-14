package com.example.capsule.ui.outfits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.capsule.R

class OutfitsFragment : Fragment() {

    private lateinit var listView : ListView

    companion object {
        private val TITLES : List<String> = listOf("Tops", "Bottoms", "Jackets", "Shoes")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_outfits, null)

        listView = v.findViewById(R.id.outfitListView)
        listView.adapter = OutfitListViewAdapter(requireActivity(), TITLES)

        return v
    }

}