package com.example.appcocktail

data class CocktailList(
    val drinks: List<Cocktail>?
)

data class Cocktail(
    val idDrink: Int,
    val strDrink: String,
    val strCategory: String,
    val strInstructions: String,
    val strIngredient1: String,
    val strIngredient2: String,
    val strIngredient3: String,
    val strIngredient4: String,
    val strIngredient5: String,
    val strMeasure1: String,
    val strMeasure2: String,
    val strMeasure3: String,
    val strMeasure4: String,
    val strMeasure5: String,
    val strDrinkThumb: String,
    val strGlass : String
)