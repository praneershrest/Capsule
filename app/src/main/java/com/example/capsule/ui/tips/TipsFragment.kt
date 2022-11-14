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

    companion object {
        // convert this into a string array in xml file later
        private val GROUP_LIST : List<String> = listOf("How to be more clothes conscious?", "How to be more clothes conscious?")
        private val CHILD_LIST: List<String> = listOf("Never gonna give you up, never gonna let you down", "Never gonna run around and hurt you")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_tips, container, false)

        expandableListView = v.findViewById(R.id.expandableListView)
        tipsExpandableListViewAdapter = TipsExpandableListViewAdapter(requireActivity(), GROUP_LIST, CHILD_LIST)
        expandableListView.setAdapter(tipsExpandableListViewAdapter)

        return v
    }
}