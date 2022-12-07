package com.example.capsule.ui.closet

import android.net.Uri

/**
 * Custom adapter for displaying images in the horizontal scrollable carousal
 * @param newImage: Uri holds the image uri to be used by adapter
 */
class SliderItem(newImage: Uri) {
    private var image: Uri

    init {
        image = newImage
    }

    fun getImage(): Uri {
        return image
    }

}