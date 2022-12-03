package com.example.capsule.ui.closet

import android.graphics.Bitmap

class SliderItem(newImage: Bitmap) {
    private var image: Bitmap

    init {
        image = newImage
    }

    fun getImage(): Bitmap {
        return image
    }

}