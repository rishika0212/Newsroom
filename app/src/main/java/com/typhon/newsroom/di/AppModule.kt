package com.typhon.newsroom.di

import com.example.newsroom.data.repository.NewsRepositoryImpl
import com.typhon.newsroom.data.remote.NewsApi
import com.typhon.newsroom.data.remote.NewsApi.Companion.BASE_URL
import com.typhon.newsroom.domain.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNewsApi():NewsApi{
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(NewsApi::class.java)
    }

    @Provides
    @Singleton
    fun providesNewRepository(newsApi:NewsApi): NewsRepository{
        return NewsRepositoryImpl(newsApi)
    }
}