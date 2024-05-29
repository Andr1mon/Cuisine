package com.example.cuisine.service

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.cuisine.service.data.FavoriteRecipe

@Dao
interface FavoriteRecipeDao {
    @Query("SELECT * FROM favoriteRecipe")
    fun getAll(): List<FavoriteRecipe>

    @Query("SELECT * FROM favoriteRecipe WHERE id = :favoriteRecipeId")
    fun findById(favoriteRecipeId: Int): FavoriteRecipe?

    @Query("SELECT * FROM favoriteRecipe WHERE title LIKE :title LIMIT 1")
    fun findByTitle(title: String): FavoriteRecipe?

    @Insert
    fun insertAll(vararg favoriteRecipes: FavoriteRecipe)

    @Insert
    fun insertOne(favouriteRecipe: FavoriteRecipe)

    @Delete
    fun delete(favoriteRecipe: FavoriteRecipe)
}