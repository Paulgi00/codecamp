package com.example.wildidle.di

import com.example.wildidle.viewmodel.TokenStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MainModule {

    @Provides
    @Singleton
    fun provideTokenStorage(): TokenStorage {
        return TokenStorage()
    }
}