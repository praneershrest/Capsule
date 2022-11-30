package com.example.capsule.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.capsule.model.Clothing
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingDatabaseDao {

    @Insert
    suspend fun insertClothing(clothing: Clothing)

    @Query("SELECT * FROM clothing_table")
    fun getAllClothing() : Flow<List<Clothing>>

    @Query("SELECT material FROM clothing_table")
    fun getAllMaterial() : Flow<List<String>>

    @Query("SELECT purchase_location FROM clothing_table")
    fun getAllPurchaseLocations() : Flow<List<String>>

    @Query("SELECT * FROM clothing_table WHERE category = :category")
    fun getAllClothingInCategory(category: String) : Flow<List<Clothing>>

    @Query("SELECT A.id, A.name, A.category, A.price, A.material, A.season, A.purchase_location, A.img_uri FROM " +
            "(SELECT C.*, count(*) as frequency " +
            "FROM clothing_table as C " +
            "LEFT JOIN clothing_history_table as CH ON C.id = CH.clothing_id " +
            "WHERE C.category=:category AND C.season=:season " +
            "GROUP BY C.id " +
            "ORDER BY frequency ASC " +
            "LIMIT 1) as A")
    fun getSuggestedClothingByCategoryForSeason(category: String, season: String) : Flow<Clothing>

    @Query("DELETE FROM clothing_table WHERE id = :id")
    fun deleteClothing(id : Long)

    @Query("DELETE FROM CLOTHING_TABLE")
    fun deleteAllClothing()
}