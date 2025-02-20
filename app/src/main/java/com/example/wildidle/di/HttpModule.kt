package com.example.wildidle.di

import android.app.Application
import android.content.Context
import com.example.wildidle.api.IdleApi
import com.example.wildidle.viewmodel.TokenStorage
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.math.BigDecimal
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
        .addInterceptor(AuthInterceptor(tokenStorage, application))
        .build()

    object BigDecimalAdapter {
        @FromJson
        fun fromJson(string: String): BigDecimal {
            try {
                return BigDecimal(string)
            } catch (e: NumberFormatException) {
                // Log the problematic value
                println("Error parsing BigDecimal from string: $string")
                return BigDecimal(0)
            }
        }

        @ToJson
        fun toJson(value: BigDecimal): String = value.toString()
    }

    @Provides
    @Singleton
    fun moshi(): Moshi = Moshi.Builder()
        .add(BigDecimalAdapter)
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

    class AuthInterceptor(
        private val tokenStorage: TokenStorage,
        private val application: Application
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val requestBuilder = chain.request().newBuilder()
            // attach access token and refresh token if available
            val token = tokenStorage.accessToken
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

            val initialResponse = chain.proceed(requestBuilder.build())
            // generate new access token with refresh token if response is 490
            if (initialResponse.code == 490) {
                initialResponse.close()
                synchronized(this)
                {
                    val httpAccessTokenClient = OkHttpClient.Builder()
                        .addInterceptor(logging)
                        .build()
                    val accessTokenRequest = Request.Builder()
                        .url("https://codecamp.comtec.eecs.uni-kassel.de/login")
                        .addHeader("Cookie", refreshToken ?: "")
                        .build()

                    httpAccessTokenClient.newCall(accessTokenRequest).execute()
                        .use { response: Response ->
                            tokenStorage.accessToken = response.headers.values("authorization")[0]
                        }

                    val retryRequestBuilder = chain.request().newBuilder()
                        .addHeader("Authorization", tokenStorage.accessToken)
                    return chain.proceed(retryRequestBuilder.build())
                }
            } else {
                return initialResponse
            }
        }
    }
}