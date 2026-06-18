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
}
