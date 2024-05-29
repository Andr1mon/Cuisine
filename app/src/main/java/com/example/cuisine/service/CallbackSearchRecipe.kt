package com.example.cuisine.service

import android.util.Log
import com.example.cuisine.service.data.Recipes
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

abstract class CallbackSearchRecipe: Callback {

    companion object {
        val TAG = "CallbackSearchRecipe"
    }

    abstract fun fireRecipe(data: Recipes)

    override fun onFailure(call: Call, e: IOException) {
        Log.i(TAG, "onFailure: ", e)
    }

    override fun onResponse(call: Call, response: Response) {
        Log.d(TAG, "onResponse: ")
        val responseData = response.body!!.string()
        Log.d(TAG, "responseData: $responseData")
        val gson = Gson()
        val data: Recipes =
            gson.fromJson(responseData, Recipes::class.java)

        Log.d(TAG, "data $data")

        fireRecipe(data)
    }
}