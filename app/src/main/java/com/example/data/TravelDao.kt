package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelDao {
    @Query("SELECT * FROM favorite_routes ORDER BY createdTimestamp DESC")
    fun getAllFavoriteRoutes(): Flow<List<FavoriteRouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRoute(route: FavoriteRouteEntity)

    @Query("DELETE FROM favorite_routes WHERE id = :routeId")
    suspend fun deleteFavoriteRoute(routeId: Int)

    @Query("SELECT * FROM favorite_places ORDER BY placeId ASC")
    fun getAllFavoritePlaces(): Flow<List<FavoritePlaceEntity>>

    @Query("SELECT * FROM favorite_places WHERE placeId = :placeId LIMIT 1")
    suspend fun getFavoritePlaceById(placeId: String): FavoritePlaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoritePlace(place: FavoritePlaceEntity)

    @Query("DELETE FROM favorite_places WHERE placeId = :placeId")
    suspend fun deleteFavoritePlace(placeId: String)

    // --- NASREDDİN HODJA PORTAL DATABASE QUERIES ---
    @Query("SELECT * FROM nasreddin_stories")
    fun getAllStoriesFlow(): Flow<List<NasreddinStoryEntity>>

    @Query("SELECT * FROM nasreddin_life_sections ORDER BY orderIndex ASC")
    fun getAllLifeSectionsFlow(): Flow<List<NasreddinLifeSectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<NasreddinStoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLifeSections(sections: List<NasreddinLifeSectionEntity>)

    @Update
    suspend fun updateStory(story: NasreddinStoryEntity)

    @Query("UPDATE nasreddin_stories SET isLiked = :liked WHERE storyId = :id")
    suspend fun toggleStoryLike(id: String, liked: Boolean)

    @Query("UPDATE nasreddin_stories SET isRead = :read WHERE storyId = :id")
    suspend fun markStoryRead(id: String, read: Boolean)

    @Query("UPDATE nasreddin_stories SET personalNotes = :notes WHERE storyId = :id")
    suspend fun updateStoryNotes(id: String, notes: String)
}
