package com.example.wildidle.di

import android.app.Application
import android.content.Context
import com.example.wildidle.data.IdleApi
import com.example.wildidle.viewmodel.TokenStorage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HttpModule {
    private const val BASE_URL = "https://codecamp.comtec.eecs.uni-kassel.de"
    private val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @Singleton
    fun okHttpClient(
        tokenStorage: TokenStorage,
        application: Application
    ) = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            val token = tokenStorage.accessToken
            println("token: ${tokenStorage.accessToken}")
            if (token.isNotEmpty()) {
                requestBuilder
                    .addHeader("Authorization", token)
            }
            val refreshToken = application.getSharedPreferences(
                "prefs",
                Context.MODE_PRIVATE
            ).getString("refresh_token", "")
            if (!refreshToken.isNullOrEmpty()) {
                requestBuilder.addHeader("Cookie", refreshToken)
            }
            chain.proceed(requestBuilder.build())
        }
        .build()

    @Provides
    @Singleton
    fun moshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun retrofit(client: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .build()

    @Provides
    @Singleton
    fun idleApiService(retrofit: Retrofit): IdleApi = retrofit.create(IdleApi::class.java)


}