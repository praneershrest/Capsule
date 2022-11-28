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

    @Query("DELETE FROM clothing_table WHERE id = :id")
    fun deleteClothing(id : Long)

    @Query("DELETE FROM CLOTHING_TABLE")
    fun deleteAllClothing()

    @Query("SELECT COUNT(*) FROM clothing_table")
    fun getClothingTableSize() : Flow<Int>
}