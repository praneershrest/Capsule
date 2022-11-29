package com.example.capsule.ui.stats

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.example.capsule.R

class FrequencyListAdapter (
    private val context: Context,
    private var list: List<ItemWearFrequency>
    ): BaseAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): ItemWearFrequency {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(
            context,
            R.layout.frequency_list_item,
            null
        )

        // Get Views for frequency list item
        val rankTextView = view.findViewById<TextView>(R.id.freq_item_rank)
        val nameTextView = view.findViewById<TextView>(R.id.freq_item_name)
        val freqTextView = view.findViewById<TextView>(R.id.freq_item_frequency)
        val itemImgView = view.findViewById<ImageView>(R.id.freq_item_image)

        val item: ItemWearFrequency = list[position]
        rankTextView.text = (position+1).toString()
        nameTextView.text = item.name
        freqTextView.text = "${item.frequency} ×"
        itemImgView.setImageURI(item.img_uri.toUri())

        return view
    }

    fun replace(newList: List<ItemWearFrequency>){
        list = newList
    }
}