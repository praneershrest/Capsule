package com.example.capsule.ui.tips

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import com.example.capsule.R

class TipsFragment : Fragment() {

    private lateinit var expandableListView: ExpandableListView
    private lateinit var tipsExpandableListViewAdapter: TipsExpandableListViewAdapter
    private lateinit var headings: List<String>
    private lateinit var childList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_tips, container, false)

        headings = activity?.resources?.getStringArray(R.array.tips_headings)?.toList()!!
        //TODO: Fill in content and convert into a string array in xml file
        childList = activity?.resources?.getStringArray(R.array.tips_headings)?.toList()!!
        expandableListView = v.findViewById(R.id.expandableListView)
        tipsExpandableListViewAdapter = TipsExpandableListViewAdapter(requireActivity(), headings, childList)
        expandableListView.setAdapter(tipsExpandableListViewAdapter)

        return v
    }
}