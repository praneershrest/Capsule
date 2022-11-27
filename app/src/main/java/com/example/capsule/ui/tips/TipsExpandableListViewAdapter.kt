package com.example.capsule.ui.tips

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.example.capsule.R

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

    override fun getGroupView(p0: Int, p1: Boolean, p2: View?, p3: ViewGroup?): View {
        val v = View.inflate(context, R.layout.group_item, null)
        val textView : TextView= v.findViewById(R.id.groupItemTextView)
        textView.text = titleList[p0]

        return textView
    }

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        val v = View.inflate(context, R.layout.child_item, null)
        val textView : TextView = v.findViewById(R.id.childItemTextView)
        textView.text = tipList[p0]
        return textView
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return false
    }
}