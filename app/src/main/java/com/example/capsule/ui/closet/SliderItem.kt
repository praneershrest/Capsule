package com.example.capsule.ui.closet

import android.net.Uri

class SliderItem(newImage: Uri) {
    private var image: Uri

    init {
        image = newImage
    }

    fun getImage(): Uri {
        return image
    }

}