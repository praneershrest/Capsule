package com.example.capsule.ui.outfits

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capsule.R
import com.example.capsule.model.Clothing

// https://developer.android.com/develop/ui/views/layout/recyclerview
// https://stackoverflow.com/questions/27194044/how-to-properly-highlight-selected-item-on-recyclerview
/**
 * Adapter that allows for horizontal scrolling of the images that
 * @property fragment fragment that this adapter was created in
 * @property clothingCategory the category of clothing such as Shirts or Jackets
 * @property clothing List of clothing entries
 * @property clothingSelectedListener  interface function that gets called whenever a user clicks on a piece of clothing
 */
class HorizontalRecyclerViewAdapter(private val fragment: Fragment,
                                    private val clothingCategory: String,
                                    private val clothing : List<Clothing>,
                                    private val clothingSelectedListener : OnClothingSelectedListener) : RecyclerView.Adapter<HorizontalRecyclerViewAdapter.ViewHolder>(){
    // set default position to be the first position or in other words, no clothes selected
    private var selectedPos = 0


    /**
     * Interface to implement when you want to do something after a user selects a piece of clothing
     */
    interface OnClothingSelectedListener {
        fun onClothingSelected(clothingCategory : String, clothingId: Long)
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

        // set the first option to be the no clothing selected option
        var clothingId : Long = -1
        if (position == 0) {
            holder.imageView.setImageResource(R.drawable.ic_baseline_not_interested_24)
        }
        // otherwise set the image view with the corresponding clothing with adjusted position
        else {
            val uri : Uri = Uri.parse(clothing[position-1].img_uri)
            Glide.with(fragment).load(uri).into(holder.imageView)
            holder.imageView.setImageURI(uri)
            clothingId = clothing[position-1].id
        }

        holder.itemView.isSelected = selectedPos == position

        // set onClickListener on the image itself and call the defined interface function
        holder.imageView.setOnClickListener {
            notifyItemChanged(selectedPos)
            selectedPos = holder.layoutPosition
            notifyItemChanged(selectedPos)
            clothingSelectedListener.onClothingSelected(clothingCategory, clothingId)
        }
    }

    // size is always at least one, accounting for no article of clothing selected
    override fun getItemCount(): Int = clothing.size + 1
}