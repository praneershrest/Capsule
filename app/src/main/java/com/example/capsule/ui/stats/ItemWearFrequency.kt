package com.example.capsule.ui.stats

data class ItemWearFrequency(
    val clothing_id: Long,
    val name: String,
    val frequency: Int,
    val img_uri: String,
    val category: String,
    val material: String,
    val season: String,
    val price: Double,
    val purchase_location: String) {

}
