package com.example.capsule.ui.closet

class SliderItem(newImage: Int) {
    private var image: Int

    init {
        image = newImage
    }

    fun getImage(): Int {
        return image
    }

}