package com.example.wildidle.data

import retrofit2.http.GET

interface IdleAPIService {
    @GET("/sign-in")
    suspend fun login(): String
}