package com.example.capsule.ui.outfits

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.capsule.R

// https://developer.android.com/develop/ui/views/layout/recyclerview
// https://stackoverflow.com/questions/27194044/how-to-properly-highlight-selected-item-on-recyclerview
class HorizontalRecyclerViewAdapter() : RecyclerView.Adapter<HorizontalRecyclerViewAdapter.ViewHolder>(){

    private var selectedPos = RecyclerView.NO_POSITION
    companion object {
        // Place holder images, will interact with repository to get actual images
        private val stuff = listOf(R.drawable.ic_outline_shopping_bag_24, R.drawable.ic_outline_shopping_bag_24, R.drawable.ic_outline_shopping_bag_24,
            R.drawable.ic_outline_shopping_bag_24,
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_notifications_black_24dp,
            R.drawable.ic_outline_shopping_bag_24,
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_notifications_black_24dp,
            R.drawable.ic_outline_shopping_bag_24,
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_notifications_black_24dp)
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val imageView : ImageView

        init {
            imageView = view.findViewById(R.id.clothingImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(stuff[position])
        holder.itemView.isSelected = selectedPos == position

        holder.imageView.setOnClickListener {
            notifyItemChanged(selectedPos)
            selectedPos = holder.layoutPosition
            notifyItemChanged(selectedPos)
            println("Position: $position ${holder.layoutPosition}")
        }
    }

    override fun getItemCount(): Int = stuff.size
}