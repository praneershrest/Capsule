package com.example.capsule.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.capsule.model.Clothing
import com.example.capsule.model.ClothingHistory

@Database(entities = [Clothing::class, ClothingHistory::class], version = 1)
abstract class ClothingDatabase : RoomDatabase() {
    abstract val clothingDatabaseDao : ClothingDatabaseDao
    abstract val clothingHistoryDatabaseDao : ClothingHistoryDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE : ClothingDatabase? = null

        fun getInstance(context : Context) : ClothingDatabase {
            var instance = INSTANCE
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext,
                    ClothingDatabase::class.java, "clothing_table").build()
                INSTANCE = instance
            }
            return instance
        }
    }

}