package com.example.capsule.ui.closet

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.capsule.R
import com.example.capsule.ui.stats.ItemWearFrequency

class ClothingListAdapter(
    private val context: Context,
    private var list: List<Pair<String, String>>,
) : BaseAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(p0: Int): Pair<String, String> {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(
            context,
            R.layout.clothing_description_item,
            null
        )
        val title = view.findViewById<TextView>(R.id.description_title)
        val description = view.findViewById<TextView>(R.id.description_value)

        val stringTuple = list.get(pos)

        title.text = stringTuple.first
        description.text = stringTuple.second

        return view

    }

}