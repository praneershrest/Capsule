package com.example.capsule.ui.tips

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import com.example.capsule.R

/**
 * Fragment to display the tips for a conscious closet
 */
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

        // Get questions and answers for tips and create views
        headings = activity?.resources?.getStringArray(R.array.tips_headings)?.toList()!!
        childList = (activity?.resources?.getTextArray(R.array.tips_content)?.toList() as List<String>?)!!
        expandableListView = v.findViewById(R.id.expandableListView)
        tipsExpandableListViewAdapter = TipsExpandableListViewAdapter(requireActivity(), headings, childList)
        expandableListView.setAdapter(tipsExpandableListViewAdapter)

        return v
    }
}