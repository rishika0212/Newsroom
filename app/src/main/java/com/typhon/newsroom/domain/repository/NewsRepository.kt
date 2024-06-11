package com.typhon.newsroom.domain.repository

import com.typhon.newsroom.domain.model.Article
import com.typhon.newsroom.util.Resource

interface NewsRepository {

    suspend fun getTopHeadlines(
        category: String
    ): Resource<List<Article>>

    suspend fun searchForNews(
        query: String
    ): Resource<List<Article>>
}