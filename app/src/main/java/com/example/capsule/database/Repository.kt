package com.example.capsule.database

import com.example.capsule.model.Clothing
import com.example.capsule.model.ClothingHistory
import com.example.capsule.ui.outfitHistory.RecentClothing
import com.example.capsule.ui.stats.ItemWearFrequency
import com.example.capsule.ui.stats.MaterialFrequency
import com.example.capsule.ui.stats.PurchaseLocationFrequency
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

    val topsFrequencies : Flow<List<ItemWearFrequency>> = clothingHistoryDatabaseDao.getItemFrequenciesForCategory("Tops")
    val bottomsFrequencies : Flow<List<ItemWearFrequency>> = clothingHistoryDatabaseDao.getItemFrequenciesForCategory("Bottoms")
    val outerwearFrequencies : Flow<List<ItemWearFrequency>> = clothingHistoryDatabaseDao.getItemFrequenciesForCategory("Outerwear")
    val shoesFrequencies : Flow<List<ItemWearFrequency>> = clothingHistoryDatabaseDao.getItemFrequenciesForCategory("Shoes")

    val topsAllFrequencies : Flow<List<ItemWearFrequency>> = clothingHistoryDatabaseDao.getAllClothingFrequencies("Tops")
    val bottomsAllFrequencies : Flow<List<ItemWearFrequency>> = clothingHistoryDatabaseDao.getAllClothingFrequencies("Bottoms")
    val outerwearAllFrequencies : Flow<List<ItemWearFrequency>> = clothingHistoryDatabaseDao.getAllClothingFrequencies("Outerwear")
    val shoesAllFrequencies : Flow<List<ItemWearFrequency>> = clothingHistoryDatabaseDao.getAllClothingFrequencies("Shoes")

    val materialFrequencies: Flow<List<MaterialFrequency>> = clothingDatabaseDao.getMaterialCount()
    val purchaseLocationFrequencies: Flow<List<PurchaseLocationFrequency>> = clothingDatabaseDao.getPurchaseLocationCount()

    fun suggestedClothingByCategoryForSeason(category: String, season: String) : Flow<Clothing> {
        return clothingDatabaseDao.getSuggestedClothingByCategoryForSeason(category, season)
    }

    fun getAllClothingBetweenDates(startDate: Long, endDate: Long) : Flow<List<RecentClothing>> {
        return clothingHistoryDatabaseDao.getAllClothingBetweenDates(startDate, endDate)
    }

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

    fun getItemFrequenciesForCategory(category: String) {
        CoroutineScope(IO).launch {
            clothingHistoryDatabaseDao.getItemFrequenciesForCategory(category)
        }
    }


}