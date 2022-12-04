package com.example.capsule.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "clothing_history_table", foreignKeys = [
    ForeignKey(
        entity = Clothing::class,
        parentColumns = ["id"],
        childColumns = ["clothing_id"],
        onDelete = CASCADE, //<<<<<
    )
])
class ClothingHistory (
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0L,

    @ColumnInfo(name="clothing_id")
    val clothingId : Long,

    @ColumnInfo(name="date")
    val date : Long = 0L,

    @ColumnInfo(name="is_suggested")
    val isSuggested : Boolean = false
)