package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavoriteRouteEntity::class, 
        FavoritePlaceEntity::class,
        NasreddinStoryEntity::class,
        NasreddinLifeSectionEntity::class
    ], 
    version = 2, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun travelDao(): TravelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aksehir_travel_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
