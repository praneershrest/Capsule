package com.example.capsule.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.capsule.model.ClothingHistory
import com.example.capsule.ui.stats.ItemWearFrequency
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingHistoryDatabaseDao {

    @Insert
    suspend fun insertClothingHistory(clothingHistory: ClothingHistory)

    @Query("SELECT * from clothing_history_table")
    fun getAllClothingHistory() : Flow<List<ClothingHistory>>

    @Query("DELETE from clothing_history_table WHERE id = :id")
    fun deleteClothingHistory(id : Long)

    @Query("DELETE from clothing_history_table")
    fun deleteAllClothingHistory()

    @Query("SELECT CH.clothing_id, C.name, C.img_uri, count(*) as frequency " +
            "FROM clothing_history_table as CH " +
            "JOIN clothing_table as C ON CH.clothing_id = C.id " +
            "WHERE C.category=:category " +
            "GROUP BY CH.clothing_id " +
            "ORDER BY frequency DESC")
    fun getItemFrequenciesForCategory(category : String): Flow<List<ItemWearFrequency>>

}