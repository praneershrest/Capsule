package com.example.capsule.database

import com.example.capsule.model.Clothing
import com.example.capsule.model.ClothingHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class Repository(private val clothingDatabaseDao : ClothingDatabaseDao,
                 private val clothingHistoryDatabaseDao: ClothingHistoryDatabaseDao) {

    val allClothing : Flow<List<Clothing>> = clothingDatabaseDao.getAllClothing()
    val allMaterials : Flow<List<String>> = clothingDatabaseDao.getAllMaterial()
    val allPurchaseLocations : Flow<List<String>> = clothingDatabaseDao.getAllPurchaseLocations()
    val allClothingHistory : Flow<List<ClothingHistory>> = clothingHistoryDatabaseDao.getAllClothingHistory()

    fun insertClothing(clothing : Clothing) {
        CoroutineScope(IO).launch {
            clothingDatabaseDao.insertClothing(clothing)
        }
    }

    fun deleteClothing(id : Long) {
        CoroutineScope(IO).launch {
            clothingDatabaseDao.deleteClothing(id)
        }
    }

    fun deleteAllClothing() {
        CoroutineScope(IO).launch {
            clothingDatabaseDao.deleteAllClothing()
        }
    }

    fun insertClothingHistory(clothingHistory: ClothingHistory) {
        CoroutineScope(IO).launch {
            clothingHistoryDatabaseDao.insertClothingHistory(clothingHistory)
        }
    }

    fun deleteClothingHistory(id : Long) {
        CoroutineScope(IO).launch {
            clothingHistoryDatabaseDao.deleteClothingHistory(id)
        }
    }

    fun deleteAllClothingHistory() {
        CoroutineScope(IO).launch {
            clothingHistoryDatabaseDao.deleteAllClothingHistory()
        }
    }


}