package com.example.capsule.ui.outfitHistory

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.capsule.R
import com.example.capsule.utils.Util

class OutfitHistoryListAdapter(private val context: Context, private var list: List<RecentClothing>) : BaseAdapter() {
    private lateinit var dateTextView: TextView
    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var imageView3: ImageView
    private lateinit var imageView4: ImageView

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View = View.inflate(
            context,
            R.layout.outfit_history_list_item,
            null
        )

        val uriStringList = ArrayList<String>()
        var show = true

        if((position-1) >= 0 && list[position-1].date == list[position].date) {
            show = false
        }

        if(show) {
            dateTextView = view.findViewById(R.id.outfit_history_date)
            imageView1 = view.findViewById(R.id.recent1)
            imageView2 = view.findViewById(R.id.recent2)
            imageView3 = view.findViewById(R.id.recent3)
            imageView4 = view.findViewById(R.id.recent4)

            dateTextView.text = Util.millisToStringFormattedDate(list[position].date)
            list.forEach {
                if(it.date == list[position].date) {
                    uriStringList.add(it.img_uri)
                }
            }

            if(uriStringList.size > 0 && uriStringList[0].isNotEmpty()) {
                Glide.with(context).load(uriStringList[0].toUri()).into(imageView1)
            }
            if(uriStringList.size > 1 && uriStringList[1].isNotEmpty()) {
                Glide.with(context).load(uriStringList[1].toUri()).into(imageView2)
            }
            if(uriStringList.size > 2 && uriStringList[2].isNotEmpty()) {
                Glide.with(context).load(uriStringList[2].toUri()).into(imageView3)
            }
            if(uriStringList.size > 3 && uriStringList[3].isNotEmpty()) {
                Glide.with(context).load(uriStringList[3].toUri()).into(imageView4)
            }
            if(!list[position].is_suggested) {
                view.findViewById<TextView>(R.id.suggested_outfit_pin).visibility = View.GONE
            }
        }
        else {
            view = View.inflate(context, R.layout.empty, null)
        }

        return view
    }

    fun replace(newList: List<RecentClothing>) {
        list = newList
    }

}