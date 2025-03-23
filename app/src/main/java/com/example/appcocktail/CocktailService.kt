package com.example.appcocktail
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface CocktailService {
    @GET("search.php")
    suspend fun getCocktailsByLetter(@Query("f") letter: Char) : CocktailList

    @GET("search.php")
    suspend fun getCocktail(@Query("s") query: String): CocktailList

    @GET("lookup.php")
    suspend fun getCocktailById(@Query("i") query : String) : CocktailList
}

object RetrofitInstance {
    private const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"
    val api: CocktailService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CocktailService::class.java)
    }
}