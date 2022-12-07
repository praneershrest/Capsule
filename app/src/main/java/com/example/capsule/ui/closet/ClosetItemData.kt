package com.example.capsule.ui.closet

/**
 * Closet Object for special query to seperate data based on category
 */
data class ClosetItemData(
    val clothing_id: Long,
    val id: Long,
    val name: String,
    val frequency: Int,
    val img_uri: String,
    val category: String,
    val material: String,
    val season: String,
    val price: Double,
    val purchase_location: String
){}
