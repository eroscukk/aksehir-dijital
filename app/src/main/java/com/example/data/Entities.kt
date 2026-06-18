package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_routes")
data class FavoriteRouteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val routeName: String,
    val routePointsJson: String, // String representation of locations in route
    val totalDistanceKm: Double,
    val totalDurationMin: Int,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_places")
data class FavoritePlaceEntity(
    @PrimaryKey val placeId: String,
    val placeNameTr: String,
    val placeNameEn: String,
    val type: String, // "HISTORICAL" or "FOOD"
    val isFavorite: Boolean = true,
    val note: String = ""
)
