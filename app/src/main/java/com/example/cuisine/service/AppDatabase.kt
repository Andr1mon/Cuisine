package com.example.cuisine.service

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cuisine.service.data.FavoriteRecipe

@Database(entities = [FavoriteRecipe::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun FavoriteRecipeDao(): FavoriteRecipeDao
}