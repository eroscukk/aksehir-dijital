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

@Entity(tableName = "nasreddin_stories")
data class NasreddinStoryEntity(
    @PrimaryKey val storyId: String,
    val titleTr: String,
    val titleEn: String,
    val storyTr: String,
    val storyEn: String,
    val wisdomTr: String,
    val wisdomEn: String,
    val audioDuration: String = "1:45",
    val isLiked: Boolean = false,
    val isRead: Boolean = false,
    val personalNotes: String = ""
)

@Entity(tableName = "nasreddin_life_sections")
data class NasreddinLifeSectionEntity(
    @PrimaryKey val sectionId: String,
    val orderIndex: Int,
    val titleTr: String,
    val titleEn: String,
    val contentTr: String,
    val contentEn: String
)

