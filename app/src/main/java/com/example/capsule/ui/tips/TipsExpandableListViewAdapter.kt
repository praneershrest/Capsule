package com.example.capsule.ui.tips

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.example.capsule.R

/**
 * Adapter that allows expandable listview to show questions and tips. Each row in expandable list view
 * contains only one item
 * @property titleList List of questions for conscious closets tips
 * @property tipList List of answers to the above questions for tips
 */
class TipsExpandableListViewAdapter(private val context : Context, private val titleList : List<String>, private val tipList : List<String>) : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return titleList.size
    }

    override fun getChildrenCount(p0: Int): Int {
        return 1
    }

    override fun getGroup(p0: Int): Any {
        return titleList[p0]
    }

    override fun getChild(p0: Int, p1: Int): Any {
        return tipList[p1]
    }

    override fun getGroupId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getChildId(p0: Int, p1: Int): Long {
        return p1.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    // Display the question to the tip
    override fun getGroupView(position: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val v = View.inflate(context, R.layout.tips_header_item, null)
        val textView : TextView = v.findViewById(R.id.groupItemTextView)
        textView.text = titleList[position]

        return textView
    }

    // Display the answer to the tip in dropdown
    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        val v = View.inflate(context, R.layout.tips_child_item, null)
        val textView : TextView = v.findViewById(R.id.childItemTextView)
        textView.text = tipList[p0]
        return textView
    }

    // make sure drop down answer is not clickable
    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return false
    }
}