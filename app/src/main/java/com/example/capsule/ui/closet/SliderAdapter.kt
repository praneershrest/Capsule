package com.example.capsule.ui.closet

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.viewpager2.widget.ViewPager2
import com.example.capsule.R
import com.example.capsule.ui.stats.ItemWearFrequency

class SliderAdapter(sliderItem: List<SliderItem>, viewPager: ViewPager2): RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {
    private lateinit var viewPager2: ViewPager2
    private lateinit var sliderItems: List<SliderItem>

    init {
        viewPager2 = viewPager
        sliderItems = sliderItem
    }


    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var imageView: ImageView
        init {
            imageView = itemView.findViewById(R.id.imageContainer)
        }

        fun setImage(sliderItem: SliderItem){
            imageView.setImageURI(sliderItem.getImage())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.slide_item_container, parent, false))
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.setImage(sliderItems.get(position))

    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }

    fun replace(newList: List<SliderItem>){
        sliderItems = newList
    }

}