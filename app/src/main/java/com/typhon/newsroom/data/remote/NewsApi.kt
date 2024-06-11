package com.typhon.newsroom.data.remote

import com.typhon.newsroom.domain.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    //https://newsapi.org/v2/top-headlines?country=us&apiKey=d46e4ed8cc6943ddae79a5bf47f888a0

    @GET("top-headlines")
    suspend fun getBreakingNews(
        @Query("category")category: String,
        @Query("country")country: String="in",
        @Query("apikey")apikey: String= API_KEY
    ): NewsResponse

    @GET("everything")
    suspend fun searchForNews(
        @Query("q")query: String,
        @Query("apikey")apikey: String= API_KEY
    ): NewsResponse


    companion object{
        const val BASE_URL="https://newsapi.org/v2/"
        const val API_KEY="d46e4ed8cc6943ddae79a5bf47f888a0"
    }
}