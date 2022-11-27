package com.example.capsule.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing_table")
data class Clothing (
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0L,

    @ColumnInfo(name = "name")
    val name : String = "",

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "price")
    val price : Double = 0.0,

    @ColumnInfo(name = "material")
    val material : String,

    @ColumnInfo(name = "season")
    val season : String = "",

    @ColumnInfo(name = "purchase_location")
    val purchase_location : String = "",

    @ColumnInfo(name = "img_uri")
    val img_uri : String = ""
)