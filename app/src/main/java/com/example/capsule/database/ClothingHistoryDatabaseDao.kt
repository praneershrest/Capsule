package com.example.capsule.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.capsule.model.ClothingHistory
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

}