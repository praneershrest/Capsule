package com.example.capsule.ui.outfitHistory

data class RecentClothing(
    val clothing_id: Long,
    val date: Long,
    val img_uri: String,
    val is_suggested: Boolean
)
