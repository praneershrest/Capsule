package com.example.capsule.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing_history_table")
class ClothingHistory (
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0L,

    @ColumnInfo(name="clothing_id")
    var clothingId : Long = 0L,

    @ColumnInfo(name="date")
    val date : String = ""
    )