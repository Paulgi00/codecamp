package com.example.wildidle.api

import com.example.wildidle.model.Item
import com.example.wildidle.model.LeaderboardResult
import com.example.wildidle.model.ScoreBody
import com.example.wildidle.model.SignInDTO
import com.example.wildidle.model.StringResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IdleApi {

    // Authentication
    @POST("/sign-up")
    suspend fun signUp(@Body signInDTO: SignInDTO): Response<StringResponse>

    @POST("/sign-in")
    suspend fun signIn(@Body signInDTO: SignInDTO): Response<StringResponse>

    @GET("/login")
    suspend fun login(): Response<StringResponse>

    // Items
    @GET("/items")
    suspend fun getItems(): Response<List<Item>>

    // Leaderboard
    @GET("/score")
    suspend fun getScore(): Response<List<LeaderboardResult>>

    @POST("/score")
    suspend fun postScore(@Body scoreBody: ScoreBody): Response<StringResponse>
}