package com.example.cuisine

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import coil.compose.rememberAsyncImagePainter
import com.example.cuisine.service.AppDatabase
import com.example.cuisine.service.CallbackSearchRecipe
import com.example.cuisine.service.RecipeService
import com.example.cuisine.service.data.FavoriteRecipe
import com.example.cuisine.service.data.Recipes
import com.example.cuisine.service.data.Result
import com.example.cuisine.ui.theme.CuisineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CuisineTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navHostController = rememberNavController()
                    AppNavHost(navHostController,applicationContext)
                }
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchBarRecipes(context: Context) {
    var text by rememberSaveable { mutableStateOf("") }
    var history by rememberSaveable { mutableStateOf("") }
    var expandedSearchBar by rememberSaveable { mutableStateOf(false) }
    val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "Recipes"
    ).allowMainThreadQueries().build()
    val expandedCard by remember { mutableStateOf(mutableMapOf<Int, Boolean>()) }


    val recipesSaver = run {
        val numberKey = "Number"
        val offsetKey = "Offset"
        val resultsKey = "Result"
        val totalResultKey = "TotalResult"
        mapSaver(
            save = { mapOf(numberKey to it.number, offsetKey to it.offset,
                resultsKey to it.results, totalResultKey to it.totalResults) },
            restore = { Recipes(it[numberKey] as Int, it[offsetKey] as Int,
                                it[resultsKey] as List<Result>, it[totalResultKey] as Int) }
        )
    }
    var recipes by rememberSaveable(stateSaver = recipesSaver) {
        mutableStateOf(Recipes(0, 0, null, 0))
    }


    Box(
        Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = text,
                    onQueryChange = { text = it },
                    onSearch = {
                        expandedSearchBar = false
                        RecipeService().searchRecipe(text, object : CallbackSearchRecipe() {
                            override fun fireRecipe(data: Recipes) {
                                Log.d("MainActivity", "data: $data")
                                recipes = data
                                history += text + "\n"
                            }
                        })

                        Log.d("MainActivity", "Recipes: $recipes")
                    },
                    expanded = expandedSearchBar,
                    onExpandedChange = { expandedSearchBar = it },
                    placeholder = { Text("Hinted search text") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
                )
            },
            expanded = expandedSearchBar,
            onExpandedChange = { expandedSearchBar = it },
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                val historyElements = history.split("\n")
                repeat(historyElements.size) { idx ->
                    if (historyElements[idx] != "") {
                        ListItem(
                            headlineContent = { Text(historyElements[idx]) },
                            // supportingContent = { Text("Number of found results: $") },
                            leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier
                                .clickable {
                                    text = historyElements[idx]
                                    expandedSearchBar = false
                                }
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.semantics { traversalIndex = 1f },
        ) {
            items(count = minOf(recipes.number, recipes.totalResults)) { index ->
                if (expandedCard[index] == null)
                    expandedCard[index] = false
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = LinearOutSlowInEasing
                            )
                        ),
                    onClick = {
                        expandedCard[index] = !expandedCard[index]!!
                        expandedSearchBar = !expandedSearchBar
                        expandedSearchBar = !expandedSearchBar
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(recipes.results!![index].image),
                                contentDescription = "Image #$index",
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(60.dp)
                            )
                            Text(
                                text = recipes.results!![index].title,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(5.dp)
                                    .fillMaxWidth(0.8f),
                                fontSize = 16.sp,

                            )
                            TextButton(
                                onClick = {
                                    val favoriteRecipe = FavoriteRecipe(
                                        recipes.results!![index].id,
                                        recipes.results!![index].image,
                                        recipes.results!![index].imageType,
                                        recipes.results!![index].title,
                                        recipes.results!![index].summary,
                                        recipes.results!![index].readyInMinutes,
                                        recipes.results!![index].healthScore,
                                        recipes.results!![index].vegetarian,
                                        recipes.results!![index].vegan,
                                        recipes.results!![index].glutenFree
                                    )
                                    if (db.FavoriteRecipeDao().findById(recipes.results!![index].id) == null)
                                        db.FavoriteRecipeDao().insertOne(favoriteRecipe)
                                    else
                                        db.FavoriteRecipeDao().delete(favoriteRecipe)
                                    expandedSearchBar = !expandedSearchBar
                                    expandedSearchBar = !expandedSearchBar
                                },
                                contentPadding = PaddingValues(5.dp)

                            ) {
                                Icon(
                                    if (db.FavoriteRecipeDao().findById(recipes.results!![index].id) == null)
                                        Icons.Filled.FavoriteBorder
                                    else
                                        Icons.Filled.Favorite,
                                    contentDescription = "FavoriteStatus",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        if (expandedCard[index]!!) {
                            Log.d("MainActivity","ExpandedCard")
                            Text(
                                text = "Score de santé : " + recipes.results!![index].healthScore + "/100\n" +
                                       "Le temps de preparation : " + recipes.results!![index].readyInMinutes + " minutes\n" +
                                       "Végétarien : " + if (recipes.results!![index].vegetarian) "Oui\n" else "Non\n" +
                                       "Vegan : " + if (recipes.results!![index].vegetarian) "Oui\n" else "Non\n" +
                                       "Sans gluten : " + if (recipes.results!![index].vegetarian) "Oui\n" else "Non\n" +
                                       "Description : " + recipes.results!![index].summary.replace(Regex("<.*?>"), "").replace(Regex("<a.*?>(.*?)</a>"), "$1"),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavHost(navHostController: NavHostController, context: Context) {
    NavHost(navHostController, startDestination = "SearchBarRecipes") {
        composable("SearchBarRecipes") {
            SearchBarRecipes(context)
            BottomMenu(navHostController)
        }
        composable("FavoriteRecipes") {
            FavoriteRecipes(context)
            BottomMenu(navHostController)
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun FavoriteRecipes(context: Context) {
    val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "Recipes"
    ).allowMainThreadQueries().build()

    var expandedCard by remember { mutableStateOf(mutableMapOf<Int, Boolean>()) }
    var favorites by remember { mutableStateOf(mutableMapOf<Int, Boolean>()) }
    val favoriteRecipes = db.FavoriteRecipeDao().getAll()

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.semantics { traversalIndex = 1f },
    ) {
        items(count = favoriteRecipes.size) { index ->
            if (expandedCard[index] == null)
                expandedCard[index] = false
            if (favorites[index] == null)
                favorites[index] = false
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = LinearOutSlowInEasing
                        )
                    ),
                onClick = {
                    expandedCard = expandedCard.toMutableMap().apply {
                        this[index] = !this[index]!!
                    }
                }
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(favoriteRecipes[index].image),
                            contentDescription = "Image #$index",
                            modifier = Modifier
                                .width(80.dp)
                                .height(60.dp)
                        )
                        Text(
                            text = favoriteRecipes[index].title.toString(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth(0.8f),
                            fontSize = 16.sp,
                        )
                        TextButton(
                            onClick = {
                                val favoriteRecipe = FavoriteRecipe(
                                    favoriteRecipes[index].id,
                                    favoriteRecipes[index].image,
                                    favoriteRecipes[index].imageType,
                                    favoriteRecipes[index].title,
                                    favoriteRecipes[index].summary,
                                    favoriteRecipes[index].readyInMinutes,
                                    favoriteRecipes[index].healthScore,
                                    favoriteRecipes[index].vegetarian,
                                    favoriteRecipes[index].vegan,
                                    favoriteRecipes[index].glutenFree
                                )
                                if (db.FavoriteRecipeDao().findById(favoriteRecipes[index].id) == null)
                                    db.FavoriteRecipeDao().insertOne(favoriteRecipe)
                                else
                                    db.FavoriteRecipeDao().delete(favoriteRecipe)
                                favorites = favorites.toMutableMap().apply {
                                    this[index] = !this[index]!!
                                }

                            },
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            Icon(
                                if (favorites[index]!!)
                                    Icons.Filled.FavoriteBorder
                                else
                                    Icons.Filled.Favorite,
                                contentDescription = "FavoriteStatus",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    if (expandedCard[index] == true) {
                        Text(
                            text = "Score de santé : " + favoriteRecipes[index].healthScore.toString() + "/100\n" +
                                    "Le temps de preparation : " + favoriteRecipes[index].readyInMinutes + " minutes",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomMenu(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button( onClick = { navController.navigate("SearchBarRecipes") }) {
                Text("Search Recipes")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button( onClick = { navController.navigate("FavoriteRecipes") }) {
                Text("Favorite Recipes")
            }
        }
    }
}