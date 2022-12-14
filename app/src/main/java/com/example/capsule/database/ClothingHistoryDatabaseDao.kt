package com.example.capsule.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.capsule.model.ClothingHistory
import com.example.capsule.ui.closet.ClosetItemData
import com.example.capsule.ui.outfitHistory.RecentClothing
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

    /**
     * get the frequency of wear for a given category
     */
    @Query("SELECT C.id, C.name, C.img_uri, count(CH.clothing_id) as frequency " +
            "FROM clothing_table C " +
            "LEFT JOIN clothing_history_table CH ON C.id = CH.clothing_id " +
            "WHERE C.category=:category " +
            "GROUP BY C.id " +
            "ORDER BY frequency DESC")
    fun getItemFrequenciesForCategory(category : String): Flow<List<ItemWearFrequency>>

    /**
     * get all clothing frequencies
     */
    @Query("SELECT CH.clothing_id, C.id, C.name, C.img_uri, C.category, C.material, C.season, C.price, C.purchase_location, count(*) as frequency " +
            "FROM clothing_table as C " +
            "LEFT JOIN clothing_history_table as CH ON C.id = CH.clothing_id " +
            "WHERE C.category=:category " +
            "GROUP BY C.id " +
            "ORDER BY C.name ASC")
    fun getAllClothingFrequencies(category: String) : Flow<List<ClosetItemData>>

    /**
     * get all clothing between a given date range
     */
    @Query("SELECT CH.clothing_id, CH.date, C.img_uri, CH.is_suggested " +
            "FROM clothing_history_table as CH " +
            "JOIN clothing_table as C ON CH.clothing_id = C.id " +
            "WHERE CH.date BETWEEN :startDate AND :endDate " +
            "ORDER BY CH.date DESC")
    fun getAllClothingBetweenDates(startDate: Long, endDate: Long) : Flow<List<RecentClothing>>
}