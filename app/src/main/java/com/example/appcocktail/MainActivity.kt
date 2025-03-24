package com.example.appcocktail

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.appcocktail.ui.theme.AppCocktailTheme
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.navigation.compose.NavHost
import androidx.navigation.NavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppCocktailTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "mainMenu") {
                    composable("mainMenu") { MainMenu(navController) }
                    composable("cocktailList") { CocktailListScreen(navController) }
                    composable("randomCocktail") { RandomCocktailScreen() }
                    composable("cocktailDetails/{cocktailId}") {
                        arg -> val cocktailId = arg.arguments?.getString("cocktailId")?:""
                        CocktailDetails(cocktailId)
                    }
                }
            }
        }
    }
}

@Composable
fun MainMenu(navController: NavController) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome to Cocktail App")
        Button(
            onClick = { navController.navigate("cocktailList") },
            modifier = Modifier
                .padding(top = 8.dp)
        ) {
            Text("LIST COCKTAILS")
        }
        Button(
            onClick = { navController.navigate("randomCocktail") },
            modifier = Modifier
                .padding(top = 8.dp)
        ) {
            Text("RANDOM COCKTAIL")
        }
    }
}

@Composable
fun CocktailListScreen(navController: NavController) {
    val apiService = RetrofitInstance.api
    var cocktails by remember { mutableStateOf<List<Cocktail>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            cocktails = emptyList()
            ('a'..'z').forEach { letter ->
                val response = apiService.getCocktailsByLetter(letter)
                cocktails += response.drinks ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("Exception", e.message.toString())
        }
    }

    val filteredCocktails = cocktails.filter{ it.strDrink.contains(searchQuery, ignoreCase = true) }

    Column {
        SearchBar(searchQuery) {searchQuery = it}
        LazyColumn {
            items(filteredCocktails) {
                cocktail -> CocktailCard(cocktail = cocktail, navController)
            }
        }
    }
}

@Composable
fun SearchBar(query : String, onQueryChanged: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        placeholder = { Text("Search cocktails") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
            shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun CocktailCard(cocktail: Cocktail, navController: NavController, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = {
                navController.navigate("cocktailDetails/${cocktail.idDrink}")
            }),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = cocktail.strDrinkThumb,
                contentDescription = cocktail.strDrink,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Fixed height for images
            )
            Text(
                text = cocktail.strDrink,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.titleMedium // Better text styling
            )
        }
    }
}

@Composable
fun CocktailDetails(cocktailId: String) {
    val apiService = RetrofitInstance.api
    var cocktail by remember { mutableStateOf<Cocktail?>(null) }

    LaunchedEffect(cocktailId) {
        try {
            cocktail = apiService.getCocktailById(cocktailId).drinks?.get(0)
        } catch (e: Exception) {
            Log.e("Exception", e.message.toString())
        }
    }

    if (cocktail == null) {
        Text(text = "Loading...")
    } else
    {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = cocktail?.strDrinkThumb,
                contentDescription = cocktail?.strDrink,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(bottom = 16.dp)
            )
            Text(
                text = cocktail?.strDrink ?: "Unknown Cocktail",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 24.sp
            )
            Text(
                text = "Category: ${cocktail?.strCategory ?: "Unknown"}",
                modifier = Modifier.padding(bottom = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp
            )
            Text(
                text = "Ingredients:",
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp
            )
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                val ingredients = listOf(
                    cocktail?.strIngredient1 to cocktail?.strMeasure1,
                    cocktail?.strIngredient2 to cocktail?.strMeasure2,
                    cocktail?.strIngredient3 to cocktail?.strMeasure3,
                    cocktail?.strIngredient4 to cocktail?.strMeasure4,
                    cocktail?.strIngredient5 to cocktail?.strMeasure5
                )
                ingredients.forEach { (ingredient, measure) ->
                    if (!ingredient.isNullOrEmpty()) {
                        Text(
                            text = "- $ingredient${measure?.let { " ($it)" } ?: ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Text(
                text = "Instructions:",
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp
            )
            Text(
                text = cocktail?.strInstructions ?: "No instructions available.",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun RandomCocktailScreen() {
    val apiService = RetrofitInstance.api
    var cocktails by remember { mutableStateOf<List<Cocktail>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val response = apiService.getRandomCocktail()
            cocktails = response.drinks ?: emptyList()
        } catch (e: Exception) {
            Log.e("Exception", e.message.toString())
        }
    }
    if(cocktails.isEmpty()){
        Text("Loading")
    } else {
        CocktailDetails(cocktails[0].idDrink.toString())
    }
}