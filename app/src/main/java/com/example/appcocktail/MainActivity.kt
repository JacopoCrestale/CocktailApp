package com.example.appcocktail

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.appcocktail.ui.theme.AppCocktailTheme
import kotlinx.coroutines.launch

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
                    composable("cocktailDetails/{cocktailId}") { arg ->
                        val cocktailId = arg.arguments?.getString("cocktailId") ?: ""
                        CocktailDetails(cocktailId)
                    }
                    composable("searchByIngredient") { SearchByIngredientsScreen(navController) }
                    composable("cocktailsByIngredient/{ingredients}") { arg ->
                        val ingredients = arg.arguments?.getString("ingredients") ?: ""
                        CocktailsListByIngredients(navController, ingredients)
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
        Button(
            onClick = { navController.navigate("searchByIngredient") },
            modifier = Modifier
                .padding(top = 8.dp)
        ) {
            Text("INGREDIENT SEARCH")
        }
    }
}

@Composable
fun CocktailListScreen(navController: NavController) {
    val apiService = RetrofitInstance.api
    var cocktails by remember { mutableStateOf<List<Cocktail>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    if(cocktails.isEmpty()) {
        LaunchedEffect(Unit) {
            coroutineScope.launch {
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
        }
    }

    val filteredCocktails =
        cocktails.filter { it.strDrink.contains(searchQuery, ignoreCase = true) }

    Column {
        SearchBar(searchQuery) { searchQuery = it }
        LazyColumn {
            items(filteredCocktails) { cocktail ->
                CocktailCard(cocktail = cocktail, navController)
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        placeholder = { Text("Search cocktails") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search Icon")
        }
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
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(cocktailId) {
        coroutineScope.launch {
            try {
                cocktail = apiService.getCocktailById(cocktailId).drinks?.get(0)
            } catch (e: Exception) {
                Log.e("Exception", e.message.toString())
            }
        }
    }

    if(cocktail == null) {
        Text(text = "Loading...")
    } else {
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
                    if(!ingredient.isNullOrEmpty()) {
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
    if(cocktails.isEmpty()) {
        Text("Loading")
    } else {
        CocktailDetails(cocktails[0].idDrink.toString())
    }
}

@Composable
fun SearchByIngredientsScreen(navController: NavController) {
    val apiService = RetrofitInstance.api
    var cocktails by remember { mutableStateOf<List<Cocktail>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    if(cocktails.isEmpty()) {
        LaunchedEffect(Unit)
        {
            coroutineScope.launch {
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
        }
    }

    val ingredients = LinkedHashSet<String>()
    for (c in cocktails) ingredients += c.getIngredients()

    var selectedIngredients by remember { mutableStateOf<List<String>>(emptyList()) }
    val searchedIngredients = ingredients
        .filter { it.contains(searchText, ignoreCase = true) }
        .toList()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search bar
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search for ingredients") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search Icon")
                }
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(searchedIngredients) { ingredient ->
                    Column {
                        IngredientItem(
                            ingredient = ingredient,
                            isSelected = selectedIngredients.contains(ingredient),
                            onSelectionChange = { selected ->
                                selectedIngredients = if(selected) {
                                    selectedIngredients + ingredient
                                } else {
                                    selectedIngredients - ingredient
                                }
                            }
                        )
                        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                    }
                }
            }
        }

        Button(
            onClick = {
                navController.navigate("cocktailsByIngredient/${selectedIngredients.joinToString(",")}")
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "SEARCH", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
fun CocktailsListByIngredients(navController: NavController, ingredients: String) {
    val apiService = RetrofitInstance.api
    var cocktails by remember { mutableStateOf<List<Cocktail>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    if(cocktails.isEmpty()) {
        LaunchedEffect(Unit) {
            coroutineScope.launch {
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
        }
    }

    var selectedIngredients = ingredients.split(",")

    val filteredCocktails = cocktails.filter { cocktail ->
        selectedIngredients.all { it in cocktail.getIngredients() }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(searchQuery) { searchQuery = it }

        if(filteredCocktails.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Cocktails Found",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredCocktails) { cocktail ->
                    CocktailCard(cocktail = cocktail, navController)
                }
            }
        }
    }
}

@Composable
fun IngredientItem(ingredient: String, isSelected: Boolean, onSelectionChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onSelectionChange(!isSelected) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ingredient,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f) // Pushes Checkbox to the right
        )

        Checkbox(
            checked = isSelected,
            onCheckedChange = { onSelectionChange(it) },
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

