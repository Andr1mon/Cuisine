package com.example.cuisine.service.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteRecipe(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "image") val image: String?,
    @ColumnInfo(name = "image_type") val imageType: String?,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "summary") val summary: String?,
    @ColumnInfo(name = "ready_in_minutes") val readyInMinutes: Int?,
    @ColumnInfo(name = "health_score") val healthScore: Int?,
    @ColumnInfo(name = "vegetarian") val vegetarian: Boolean,
    @ColumnInfo(name = "vegan") val vegan: Boolean?,
    @ColumnInfo(name = "gluten_free") val glutenFree: Boolean?,
)
