package com.example.newsroom.data.repository

import com.typhon.newsroom.data.remote.NewsApi
import com.typhon.newsroom.domain.model.Article
import com.typhon.newsroom.domain.repository.NewsRepository
import com.typhon.newsroom.util.Resource

class NewsRepositoryImpl(
    private val newsApi: NewsApi
): NewsRepository {

    override suspend fun getTopHeadlines(category: String): Resource<List<Article>> {
        return try {
            val response = newsApi.getBreakingNews(category = category)
            Resource.Success(data = response.articles)
        } catch (e: Exception) {
            Resource.Error(message = "Failed to fetch news ${e.message}")
        }
    }

    override suspend fun searchForNews(query: String): Resource<List<Article>> {
        return try {
            val response = newsApi.searchForNews(query = query)
            Resource.Success(data = response.articles)
        } catch (e: Exception) {
            Resource.Error(message = "Failed to fetch news ${e.message}")
        }
    }
}