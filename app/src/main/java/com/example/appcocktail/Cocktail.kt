package com.example.appcocktail

data class CocktailList(
    val drinks: List<Cocktail>?
)

data class Cocktail(
    val idDrink: Int,
    val strDrink: String,
    val strCategory: String?,
    val strInstructions: String?,
    val strDrinkThumb: String?,

    val strIngredient1: String?,
    val strIngredient2: String?,
    val strIngredient3: String?,
    val strIngredient4: String?,
    val strIngredient5: String?,
    val strIngredient6: String?,
    val strIngredient7: String?,
    val strIngredient8: String?,
    val strIngredient9: String?,
    val strIngredient10: String?,
    val strIngredient11: String?,
    val strIngredient12: String?,
    val strIngredient13: String?,
    val strIngredient14: String?,
    val strIngredient15: String?,

    val strMeasure1: String?,
    val strMeasure2: String?,
    val strMeasure3: String?,
    val strMeasure4: String?,
    val strMeasure5: String?,
    val strMeasure6: String?,
    val strMeasure7: String?,
    val strMeasure8: String?,
    val strMeasure9: String?,
    val strMeasure10: String?,
    val strMeasure11: String?,
    val strMeasure12: String?,
    val strMeasure13: String?,
    val strMeasure14: String?,
    val strMeasure15: String?,

    val strGlass: String?,
    val strAlcoholic: String?
){
    fun getIngredients(): LinkedHashSet<String> {
        return linkedSetOf(
            this.strIngredient1,
            this.strIngredient2,
            this.strIngredient3,
            this.strIngredient4,
            this.strIngredient5,
            this.strIngredient6,
            this.strIngredient7,
            this.strIngredient8,
            this.strIngredient9,
            this.strIngredient10,
            this.strIngredient11,
            this.strIngredient12,
            this.strIngredient13,
            this.strIngredient14,
            this.strIngredient15
        ).filterNotNull().toCollection(LinkedHashSet())
    }
}

