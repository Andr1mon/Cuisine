package com.example.cuisine.service

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request

class RecipeService {
    companion object {
        val TAG = "DeezerService"
        var client = OkHttpClient()
        // API_KEY for spoonacular.com
        val API_KEY = "8138508d4e494f17a91ca9aac897433f"
    }

    fun searchRecipe(query: String, callback: CallbackSearchRecipe) {
        Log.d(TAG, "searchRecipe: $query")

        val request: Request = Request.Builder()
            .url("https://api.spoonacular.com/recipes/complexSearch?apiKey=$API_KEY&addRecipeInformation=true&query=$query")
            .build()

        client.newCall(request).enqueue(callback)

    }
}