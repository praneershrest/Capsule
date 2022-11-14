package com.example.capsule.ui.outfits

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capsule.R

class OutfitListViewAdapter(private val context : Context, private val titles : List<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return titles.size
    }

    override fun getItem(p0: Int): Any {
        return titles[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val v : View = View.inflate(context, R.layout.outfits_row_item, null)

        val textView : TextView = v.findViewById(R.id.outfitRowTitleTextView)
        val recyclerView : RecyclerView = v.findViewById(R.id.outfitRowRecyclerView)

        textView.text = titles[p0]

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = HorizontalRecyclerViewAdapter()

        return v
    }
}