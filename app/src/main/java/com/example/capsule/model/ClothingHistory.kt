package com.example.capsule.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing_history_table")
class ClothingHistory (
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0L,

    @ColumnInfo(name="top_id")
    val top_id : Long = 0L,

    @ColumnInfo(name="bottom_id")
    val bottom_id : Long = 0L,

    @ColumnInfo(name="outerwear_id")
    val outerwear_id : Long = 0L,

    @ColumnInfo(name="shoe_id")
    val shoe_id : Long = 0L,

    @ColumnInfo(name="date")
    val date : String = ""
    )